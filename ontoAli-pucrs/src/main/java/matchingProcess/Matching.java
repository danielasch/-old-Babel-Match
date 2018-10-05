package matchingProcess;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSynset;
import objects.MatchingObject;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import edu.mit.jwi.item.ISynset;
import objects.Concept;
import objects.ConceptManager;
import objects.Ontology;
import resources.BabelNetResource;
import resources.Utilities;

import javax.xml.bind.SchemaOutputResolver;

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
	//Synset found at disambiguation process
	private BabelSynset goodSynset;

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
				"\t\t\t<relation>" + m.get_relation().toString() + "</relation>\n" +
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


	public void matchBabel(List<Concept> dom, List<Concept> up) {
		init_log();
		ConceptManager man = new ConceptManager();
		List<Mapping> listM = new ArrayList<>();
		List<BabelSynset> hyp = new LinkedList<>();
		Set<BabelSynset>searched = new HashSet<>();
		int idx, levels, outdated, limit;
		Boolean matched;
		limit = 4;
		for (Concept d : dom) {
			hyp.clear();
			searched.clear();
			idx = 0;
			levels = 1;
			matched = false;
			if(d.get_goodSynset() != null) {
				System.out.println("-------------------------------------");
				System.out.println("domain : " + d.get_className());
				BabelSynset bs = d.get_goodSynset().getSynset();
				System.out.println("Best synset: " + bs + " " + bs.getID() + " " + bs.getMainSense());
				hyp.add(bs);
				while (idx < hyp.size()) {
					if (levels == limit) {
						System.out.println("\nCOULD NOT MATCH!");
						System.out.println("END OF VERIFICATION");
						break;
					}
					String hypernym = bn.lemmatizeHypernym(hyp.get(idx).toString());
					for (Concept u : up) {
						if (u.get_className().toLowerCase().equals(hypernym)) {
							Mapping map = new Mapping();
							map.set_source(d.get_owlClass().getIRI().toString());
							man.config_aliClass(d, u.get_owlClass());
							map.set_target(d.get_aliClass().getIRI().toString());
							map.set_relation("&lt;");
							map.set_measure("1.0");
							listM.add(map);
							//System.out.println(hypernym + " : " + map.get_source() + ", " + map.get_target());
							Utilities ut = d.get_utilities();
							ut.set_hypernyms(hyp.toString());
							ut.setSelected_hypernym(hypernym);
							//System.out.println("*******" + hyp);
							ut.setIdx(idx);
							ut.setLevel(levels);
							matched = true;
							break;
						}
					}
					if (matched) {
						System.out.println("\nCOMPLETE HYPENYMS: " + hyp);
						System.out.println("MATCHED WIHT: " + hypernym + " - " + hyp + " on level " + levels + " at index " + idx + "\n\n");
						break;
					} else if (idx == hyp.size() - 1) {
						System.out.println("\nCOULD NOT MATCH WITH: " + hyp + " on level " + levels);
						if(levels+1!=limit) {
							System.out.println("\nEXPANDING...");
							outdated = hyp.size();
							for (int i = 0; i < outdated; i++) {
								BabelSynset to_search = hyp.get(i);
								hyp = bn.getHypernyms(to_search, hyp);
								searched.add(to_search);
								System.out.println(hyp);
							}
							hyp = modify(outdated, hyp, searched);
							idx -= outdated;
							if(hyp.isEmpty()) levels = limit-1;
						}
						else idx--;
						levels++;
					}
					idx++;
				}
			}
		}
		this.listMap = listM;
		final_log();
	}

	public List<BabelSynset> modify(int index, List<BabelSynset>hypernyms, Set<BabelSynset>searched ){
		System.out.println(hypernyms.toString());
		for(int i = 0; i < index; i++){
			hypernyms.remove(0);
			System.out.println(hypernyms.toString());
		}
		if(hypernyms.isEmpty()) return hypernyms;
		System.out.println("\nCleared hyp: " + hypernyms.toString());
		for(int i = 0; i < index; i++){
			BabelSynset bs = hypernyms.get(i);
			if(searched.contains(bs)){
				hypernyms.remove(bs);
				i--;
			}
		}
		System.out.println("Modifyed hyp: " + hypernyms.toString());
		return parse(hypernyms);
	}

	public List<BabelSynset> parse(List<BabelSynset>hypernyms){
		Set<BabelSynset>set = new HashSet<>();
		for(BabelSynset b:hypernyms){
			set.add(b);
		}
		hypernyms.clear();
		for(BabelSynset b:set){
			hypernyms.add(b);
		}
		System.out.println("Parsed hyp: " + hypernyms.toString());
		return hypernyms;
	}


	/*


	public void match(List<Concept> listDom, List<Concept> listUp) {
		init_log();
		for(Concept cnp: listDom) {
			System.out.println(cnp.get_className());
			if(cnp.get_goodSynset() != null) {
				System.out.println("\nStarting match...\n");
				System.out.println("------------------------------");
				matchHyp(cnp, listUp);
				System.out.println("------------------------------\n");
			}
		}
		final_log();
	}

	private void matchHyp(Concept cnp, List<Concept> listUp) {
		ConceptManager man = new ConceptManager();
		Map<Concept, Integer> map = new HashMap<>();
		goodSynset = cnp.get_goodSynset().getSynset();
		System.out.println("Good Synset: " + goodSynset.getMainSense());
		int level = 0;
		MatchingObject mo = new MatchingObject(goodSynset, level);
		mo.create_list();
		Concept align = findHypers(goodSynset, listUp, null, level, mo);
		if(align != null) {
			Mapping mappin = new Mapping();
			man.config_aliClass(cnp, align.get_owlClass());
			//man.config_object(cnp, ooList);
			mappin.set_source(cnp.get_classID());
			mappin.set_target(align.get_classID());
			mappin.set_measure("1.0");
			mappin.set_relation("&lt;");
			this.listMap.add(mappin);
		}
		man.config_object(cnp, mo);
	}

	private Concept searchTop(BabelSynset synset, List<Concept> listUp) {
		String sense = synset.getMainSense().toString();
		for(Concept up: listUp) {
			if(up.get_className().toLowerCase().equals(sense.toLowerCase())) {
				return up;
			}
		}
		return null;
	}


	private Concept findHypers(BabelSynset synset, List<Concept> listUp,Concept aligned, int level, MatchingObject mo) {
		if(aligned!=null) return aligned;
		if(level < 10){
			if(level > 0 && synset == goodSynset) return null;
			else{
				List<BabelSynset> hyp = bn.getHypernyms(synset);
				if (!hyp.isEmpty()) {
					System.out.println("\nHypernyms of " + synset.getMainSense() + ": " + hyp.toString());
					System.out.println("Level: " + level + "\n");
					level++;
					mo.create_list();
					for (BabelSynset bs : hyp) {
						MatchingObject nmo = new MatchingObject(bs, level);
						mo.add_list(nmo);
						aligned = searchTop(bs, listUp);
						if (aligned != null) {
							System.out.println("\nMatched!\n");
							return aligned;
						}
					}
					for(MatchingObject nmo: mo.get_list()) {
						if(aligned != null) return aligned;
						findHypers(nmo.getSynset(), listUp, aligned, level, nmo);
					}
				}
			}
		}
		return null;
	}
	*/

	
}

