package matchingProcess;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import objects.Concept;
import objects.ConceptManager;
import objects.Ontology;
import objects.OutObjectWE;
import resources.BaseResource;

	
/**
 * This class matches Domain Ont. classes with Top Ont. classes
 */
public class MatchingWE {

//Attributes
		
		//Map list
		private List<Mapping> listMap;
		//path to write the rdf file
		private String localfile;
		private BaseResource baseresource;


//Constructor
		
		public MatchingWE(String _file, BaseResource br) {
			log();
			listMap = new ArrayList<Mapping>();
			localfile = _file;
			baseresource = br;
		}


//Log methods
		
		private void log() {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Word Embeddings Matcher selected!" );
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
					"\t\t\t<relation>" + m.getRelation().toString() + "</relation>\n" +
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


		public void match(List<Concept> listDom, List<Concept> listUp) {
			initLog();
			for(Concept cnpDom: listDom) {
				Set<String> contextDom = cnpDom.getContext();
				double max = 0;
				Concept align = null;
				ConceptManager man = new ConceptManager();
				int sizeDom = contextDom.size();
				
				List<OutObjectWE> ooList = new ArrayList<>();
				int aux = 0;

				for(Concept cnpUp: listUp) {
					OutObjectWE oo = new OutObjectWE(sizeDom);
					Set<String> contextUp = cnpUp.getContext();
					double mediaT = 0;
					int sizeUp = contextUp.size();
					
					HashMap<String, Object> map = new HashMap<>();
					Double[] vec = new Double[sizeDom];
					int aux_1 = 0;
					for(String elDom: contextDom) {
						double media = 0;
						
						HashMap<String, Double> map_1 = new HashMap<>();
						
						for(String elUp: contextUp) {
							double sim = similarity(elDom, elUp);
							media = media + sim;
							
							map_1.put(elUp, sim);	
						}
						
						media = media / sizeUp;
						mediaT = mediaT + media;
						vec[aux_1] = media;
						map.put(elDom, map_1);
						aux_1++;
					}
					
					mediaT = mediaT / sizeDom;
					
					if(mediaT > max) {
						max = mediaT;
						align = cnpUp;
					}
					
					//System.out.println("============OO==============");
					//System.out.println(cnpUp);
					oo.setTopConcept(cnpUp);
					//System.out.println(map);
					oo.setMap(map);
					//System.out.println(vec);
					oo.setVector(vec);
					//System.out.println(mediaT);
					oo.setTotalAverage(mediaT);
					
					if(aux < 5) {
						ooList.add(oo);
						Collections.sort(ooList);
						aux++;
					} else {
						ooList.add(oo);
						Collections.sort(ooList);
						ooList.remove(4);
						aux++;
					}
				}
				
				Mapping map = new Mapping();
				man.configAliClass(cnpDom, align.get_owlClass());
				man.configObject(cnpDom, ooList);
				map.setSource(cnpDom.getClassID());
				map.setTarget(align.getClassID());
				map.setMeasure("1.0");
				map.setRelation("&lt;");
				this.listMap.add(map);			
			}
			finalLog();
		}


		public void matchInv(List<Concept> listDom, List<Concept> listUp) {
			initLog();
			for(Concept cnpDom: listDom) {
				Set<String> contextDom = cnpDom.getContext();
				double max = 0;
				Concept align = null;
				ConceptManager man = new ConceptManager();
				int sizeDom = contextDom.size();
				
				List<OutObjectWE> ooList = new ArrayList<>();
				int aux = 0;

				for(Concept cnpUp: listUp) {
					OutObjectWE oo = new OutObjectWE(sizeDom);
					Set<String> contextUp = cnpUp.getContext();
					double mediaT = 0;
					int sizeUp = contextUp.size();
					
					HashMap<String, Object> map = new HashMap<>();
					Double[] vec = new Double[sizeUp];
					int aux_1 = 0;
					for(String elUp: contextUp) {
						double media = 0;
						
						HashMap<String, Double> map_1 = new HashMap<>();
						
						for(String elDom: contextDom) {
							double sim = similarity(elDom, elUp);
							media = media + sim;
							
							map_1.put(elDom, sim);	
						}
						
						media = media / sizeDom;
						mediaT = mediaT + media;
						vec[aux_1] = media;
						map.put(elUp, map_1);
						aux_1++;
					}
					
					mediaT = mediaT / sizeUp;
					
					if(mediaT > max) {
						max = mediaT;
						align = cnpUp;
					}
					
					//System.out.println("============OO==============");
					//System.out.println(cnpUp);
					oo.setTopConcept(cnpUp);
					//System.out.println(map);
					oo.setMap(map);
					//System.out.println(vec);
					oo.setVector(vec);
					//System.out.println(mediaT);
					oo.setTotalAverage(mediaT);
					
					if(aux < 5) {
						ooList.add(oo);
						Collections.sort(ooList);
						aux++;
					} else {
						ooList.add(oo);
						Collections.sort(ooList);
						ooList.remove(4);
						aux++;
					}
				}
				
				Mapping map = new Mapping();
				man.configAliClass(cnpDom, align.get_owlClass());
				man.configObject(cnpDom, ooList);
				map.setSource(cnpDom.getClassID());
				map.setTarget(align.getClassID());
				map.setMeasure("1.0");
				map.setRelation("&lt;");
				this.listMap.add(map);			
			}
			finalLog();
		}


		private double similarity(String elDom, String elUp) {
			double sim = this.baseresource.getWord2Vec().getword2Vec().similarity(elDom, elUp);
			if(!(Double.isNaN(sim))) {
				//increment the media and adds the similarity
				return (sim * 10);
            } else {
            	//case similarity is null
            	return 0;
            }
		}

}
