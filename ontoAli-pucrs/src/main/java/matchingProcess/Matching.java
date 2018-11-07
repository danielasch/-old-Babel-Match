package matchingProcess;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import it.uniroma1.lcl.babelnet.BabelSynset;

import objects.Concept;
import objects.ConceptManager;
import objects.Ontology;
import resources.BabelNetResource;
import resources.BaseResource;
import resources.Utilities;
import synsetSelection.SynsetDisambiguation;

/**
 * This class matches Domain Ont. classes with Top Ont. classes
 */
public class Matching {

//Attributes

	//Map list
	private List<Mapping> listMap;
	//path to write the rdf file
	private String localfile;
	//BabelNet manipulation class
	private BabelNetResource bn;
	//Base resource for disambiguation process
	private BaseResource base = new BaseResource(1, null);
	//Lesk process class
	private SynsetDisambiguation disamb = new SynsetDisambiguation(base);


//Constructor	
	
	public Matching(String _file) {
		log();
		this.listMap = null;
		this.localfile = _file;
		this.bn = new BabelNetResource();
	}


//Log methods	
	
	private void log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Matcher selected!" );
	}
	
	private void initLog() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Matching ontologies..." );
	}
	
	private void finalLog() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Ontologies matched!" );
	}
	
	private void outLog() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - RDF file generated!" );
	}


//Methods
	
	/**
	 * Turn the mapping class into a string
	 * to write the rdf file
	 */
	private String toRdf(Mapping m) {
		
		String out = "\t<map>\n" +
				"\t\t<Cell>\n" +
				"\t\t\t<entity1 rdf:resource='"+ m.getTarget() +"'/>\n" +
				"\t\t\t<entity2 rdf:resource='"+ m.getSource() +"'/>\n" +
				"\t\t\t<relation>" + m.getRelation() + "</relation>\n" +
				"\t\t\t<measure rdf:datatype='http://www.w3.org/2001/XMLSchema#float'>"+ m.getMeasure() +"</measure>\n" +
				"\t\t</Cell>\n" + "\t</map>\n";
		return out;		
	}


	/**
	 * Writes the rdf file
	 */
	public void outRdf(Ontology onto1, Ontology onto2) {
		
		try {
			FileWriter arq = new FileWriter(localfile);
			PrintWriter print = new PrintWriter(arq);
		
			print.print("<?xml version='1.0' encoding='utf-8' standalone='no'?>\n" + 
						"<rdf:RDF xmlns='http://knowledgeweb.semanticweb.org/heterogeneity/alignment#'\n" +
						"\t\t xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'\n" +
						"\t\t xmlns:xsd='http://www.w3.org/2001/XMLSchema#'\n" + 
						"\t\t xmlns:align='http://knowledgeweb.semanticweb.org/heterogeneity/alignment#'>\n");
		
			print.print("<Alignment>\n" + 
						"\t<xml>yes</xml>\n" + 
						"\t<level>0</level>\n" + 
						"\t<type>11</type>\n");
		
			print.print("\t<onto1>\n" + "\t\t<Ontology rdf:about=" + '"' + onto2.getOntologyID().getOntologyIRI().toString() + '"' + ">\n" +
						"\t\t\t<location>file:" + onto2.getFileName() + "</location>\n" +
							"\t\t</Ontology>\n" + "\t</onto1>\n");
		
			print.print("\t<onto2>\n" + "\t\t<Ontology rdf:about=" + '"' + onto1.getOntologyID().getOntologyIRI().toString() + '"' + ">\n" +
				"\t\t\t<location>file:" + onto1.getFileName() + "</location>\n" +
					"\t\t</Ontology>\n" + "\t</onto2>\n");
		
			for(Mapping m: listMap) {
				if(!m.getMeasure().equals("false")) {
					print.print(toRdf(m));
				}
			}
		
			print.print("</Alignment>\n" + "</rdf:RDF>");
		
			arq.close();
			outLog();
		} catch(IOException e) {
			System.out.println("Operacão I/O interrompida, no arquivo de saída .RDF!");
	    	System.out.println("erro: " + e);
			
		}
	}


	/**
	 * Matches a pair of concepts (domain - top) through
	 * the babelnet's hypernym structure recovered from
	 * the synset assigned to the domain concept
	 */
	public void matchBabel(List<Concept>dom, List<Concept>up){
		initLog();
		List<BabelNetResource.SearchObject> hyp = new LinkedList<>();
		List<BabelSynset> path = new LinkedList<>();
		List<Mapping> listM = new ArrayList<>();
		List<String> match_context = new LinkedList<>();
        BabelSynset selected;
		for(Concept d : dom){
			path.clear();
			match_context.clear();
			match_context.addAll(d.getContext());
			int levels = 0;
			int limit = 10;
			Boolean matched;
			if(d.getGoodSynset() != null) {
                System.out.println("\n-------------------------------------");
				System.out.println("Domain concept: " + d.getClassName());
				BabelSynset bs = d.getGoodSynset().getSynset();
                String hypernym = bn.lemmatizeHypernym(bs.toString());
                matched = tryMatch(hypernym,up,d,listM,levels);
                path.add(bs);
				while(levels != limit){
                    if(matched) {
                        d.getUtilities().setHypernyms(path.toString());
                        break;
                    }
                    System.out.println("Best synset: " + bs + " " + bs.getID() + " " + bs.getMainSense());
                    System.out.println("Path: " + path);
                    hyp = bn.getHypernyms(bs,hyp,path);
                    System.out.println("Hypernyms: " + hyp.toString());
                    selected = disamb.leskTechnique(hyp,match_context).getSynset();
                    hyp.clear();
                    hypernym = bn.lemmatizeHypernym(selected.toString());
                    matched = tryMatch(hypernym,up,d,listM,levels);
                    levels++;
                    bs = selected;
                    path.add(bs);
                    System.out.println("\n");
				}
				if(!matched) System.out.println("Could not match!\n");
			}
		}
		this.listMap = listM;
		finalLog();
	}


	public boolean tryMatch(String hypernym, List<Concept>up, Concept d, List<Mapping> listM, int levels){
		Boolean matched = false;
		ConceptManager man = new ConceptManager();
		for (Concept u : up) {
			if (u.getClassName().toLowerCase().equals(hypernym)) {
				Mapping map = new Mapping();
				map.setSource(d.get_owlClass().getIRI().toString());
				man.configAliClass(d, u.get_owlClass());
				map.setTarget(d.getAliClass().getIRI().toString());
				map.setRelation("&lt;");
				map.setMeasure("1.0");
				listM.add(map);
				Utilities ut = d.getUtilities();
				ut.setSelectedHypernym(hypernym);
				ut.setLevel(levels);
                System.out.println("\nMatched with: " + u.getClassName());
				matched = true;
				break;
			}
		}
		return matched;
	}
	
}

