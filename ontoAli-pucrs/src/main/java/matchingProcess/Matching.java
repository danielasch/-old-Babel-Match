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

/*
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
	
	private void init_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Matching ontologies..." );
	}
	
	private void final_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Ontologies matched!" );
	}
	
	private void out_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - RDF file generated!" );
	}

//Methods
	
	/*
	 * Turn the mapping class into a string
	 * to write the rdf file
	 */
	private String toRDF(Mapping m) {
		
		String out = "\t<map>\n" +
				"\t\t<Cell>\n" +
				"\t\t\t<entity1 rdf:resource='"+ m.get_target() +"'/>\n" +
				"\t\t\t<entity2 rdf:resource='"+ m.get_source() +"'/>\n" +
				"\t\t\t<relation>" + m.get_relation() + "</relation>\n" +
				"\t\t\t<measure rdf:datatype='http://www.w3.org/2001/XMLSchema#float'>"+ m.get_measure() +"</measure>\n" +
				"\t\t</Cell>\n" + "\t</map>\n";
		return out;		
	}
	
	/*
	 * Writes the rdf file
	 */
	public void out_rdf(Ontology onto1, Ontology onto2) {
		
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
		
			print.print("\t<onto1>\n" + "\t\t<Ontology rdf:about=" + '"' + onto2.get_ontologyID().getOntologyIRI().toString() + '"' + ">\n" + 
						"\t\t\t<location>file:" + onto2.get_fileName() + "</location>\n" + 
							"\t\t</Ontology>\n" + "\t</onto1>\n");
		
			print.print("\t<onto2>\n" + "\t\t<Ontology rdf:about=" + '"' + onto1.get_ontologyID().getOntologyIRI().toString() + '"' + ">\n" + 
				"\t\t\t<location>file:" + onto1.get_fileName() + "</location>\n" + 
					"\t\t</Ontology>\n" + "\t</onto2>\n");
		
			for(Mapping m: listMap) {
				if(!m.get_measure().equals("false")) {
					print.print(toRDF(m));
				}
			}
		
			print.print("</Alignment>\n" + "</rdf:RDF>");
		
			arq.close();
			out_log();
		} catch(IOException e) {
			System.out.println("Operacão I/O interrompida, no arquivo de saída .RDF!");
	    	System.out.println("erro: " + e);
			
		}
	}

	/*
	 * Matches a pair of concepts (domain - top) through
	 * the babelnet's hypernym structure recovered from
	 * the synset assigned to the domain concept
	 */



	public void matchBabel(List<Concept>dom, List<Concept>up){
		init_log();
		List<BabelNetResource.SearchObject> hyp = new LinkedList<>();
		List<BabelSynset> path = new LinkedList<>();
		List<Mapping> listM = new ArrayList<>();
		List<String> match_context = new LinkedList<>();
        BabelSynset selected;
		for(Concept d : dom){
			path.clear();
			match_context.clear();
			match_context.addAll(d.get_context());
			int levels = 0;
			int limit = 10;
			Boolean matched;
			if(d.get_goodSynset() != null) {
                System.out.println("\n-------------------------------------");
				System.out.println("Domain concept: " + d.get_className());
				BabelSynset bs = d.get_goodSynset().getSynset();
                String hypernym = bn.lemmatizeHypernym(bs.toString());
                matched = try_match(hypernym,up,d,listM,levels);
                path.add(bs);
				while(levels != limit){
                    if(matched) {
                        d.get_utilities().set_hypernyms(path.toString());
                        break;
                    }
                    System.out.println("Best synset: " + bs + " " + bs.getID() + " " + bs.getMainSense());
                    System.out.println("Path: " + path);
                    hyp = bn.getHypernyms(bs,hyp,path);
                    System.out.println("Hypernyms: " + hyp.toString());
                    selected = disamb.leskTechnique(hyp,match_context).getSynset();
                    hyp.clear();
                    hypernym = bn.lemmatizeHypernym(selected.toString());
                    matched = try_match(hypernym,up,d,listM,levels);
                    levels++;
                    bs = selected;
                    path.add(bs);
                    System.out.println("\n");
				}
				if(!matched) System.out.println("Could not match!\n");
			}
		}
		this.listMap = listM;
		final_log();
	}

	public boolean try_match(String hypernym, List<Concept>up, Concept d, List<Mapping> listM, int levels){
		Boolean matched = false;
		ConceptManager man = new ConceptManager();
		for (Concept u : up) {
			if (u.get_className().toLowerCase().equals(hypernym)) {
				Mapping map = new Mapping();
				map.set_source(d.get_owlClass().getIRI().toString());
				man.config_aliClass(d, u.get_owlClass());
				map.set_target(d.get_aliClass().getIRI().toString());
				map.set_relation("&lt;");
				map.set_measure("1.0");
				listM.add(map);
				Utilities ut = d.get_utilities();
				ut.setSelected_hypernym(hypernym);
				ut.setLevel(levels);
                System.out.println("\nMatched with: " + u.get_className());
				matched = true;
				break;
			}
		}
		return matched;
	}
	
}

