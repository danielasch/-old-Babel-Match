package resources;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma1.lcl.babelnet.BabelSynset;
import objects.Concept;

/*
 * This class generates the text files
 */
public class OutFiles {
	
//Attributes
	
	//Contains path to write the text files
	private String outPath;

//Constructor	
	
	/*
	 * This constructor receive the path to the alignment and
	 * change .rdf by _1.text, this way, the text file will be
	 * generated at the save folder as the alignment
	 */
	public OutFiles(String path) {
		String aux = path.replace(".rdf", "_1.txt");
		this.outPath = aux;
	}
	
//Methods
	
	/*
	 * Generates the text file for the overlapping technique
	 */
	public void out_file(List<Concept> listDomain) {
		try {
			FileWriter arq = new FileWriter(this.outPath);
			PrintWriter printer = new PrintWriter(arq);
			List<String> bgwSelect;
			for(Concept cnp: listDomain) {
				printer.print("\nNome do conceito de domínio: " + cnp.get_className() + "\n");
				printer.print("Descrição: " + cnp.get_desc() + "\n");
				printer.print("Supers: " + cnp.get_supers() + "\n");
				printer.print("Subs: " + cnp.get_subs() + "\n");
				printer.print("Contexto Domínio conceito: " + cnp.get_context() + "\n");
				printer.print("Conceito Topo alinhado: " + cnp.get_aliClass() + "\n");
				if(cnp.get_goodSynset() != null) {
					printer.print("Synset selecionado BabelNet: " + cnp.get_goodSynset().getSynset().toString() + "\n");
					printer.print("\nHiperonímio selecionado: " + cnp.get_utilities().getSelected_hypernym() +
							" no nível de busca " + cnp.get_utilities().getLevel() + " e índice " +
							cnp.get_utilities().getIdx() + "\n");
					printer.print("Número de Synsets recuperados: " + cnp.get_utilities().get_numSy() + "\n\n");
					printer.print("Conjunto de synsets recuperados:\n");
					List<BabelNetResource.SearchObject> synsets = cnp.get_utilities().get_synsetCntx();
					for (BabelNetResource.SearchObject so : synsets) {
						printer.print("\n>Synset: " + so.getSynset() + "\n");
						printer.print(">Sentidos: " + so.getSenses() + "\n");
						printer.print(">Glosses: " + so.getGlosses() + "\n");
						printer.print(">BOW: " + so.getBgw().toString() + "\n");
					}
					printer.print("\n");
					bgwSelect = cnp.get_goodSynset().getBgw();
					printer.print("Intersecção de palavras:");
					for (String a : bgwSelect) {
						if (cnp.get_context().contains(a)) {
							printer.print(" " + a + " ");
						}
					}
					printer.print("\n--------------------------------------------------------------------\n");
				}
				else{
					printer.print("Não foi possível encontar synsets para esse conceito!");
				}
			}
		arq.close();
		} catch(IOException e) {
			System.out.println("Operação I/O interrompida, no arquivo de saída syCNTXT!");
	    	System.out.println("erro: " + e);
		}
	}

	/*
	 * Generates the text file for the Word Embeddings technique
	 */
	public void out_file_we_wn(List<Concept> listDomain) {
		try {
			FileWriter arq = new FileWriter(this.outPath);
			PrintWriter printer = new PrintWriter(arq);
			
			for(Concept cnp: listDomain) {
				printer.print("Nome do conceito de domínio: " + cnp.get_className() + "\n");
				printer.print("Descrição: " + cnp.get_desc() + "\n");
				printer.print("Supers: " + cnp.get_supers() + "\n");
				printer.print("Subs: " + cnp.get_subs() + "\n");
				printer.print("Contexto: " + cnp.get_context() + "\n");
				printer.print("Conceito Topo alinhado: " + cnp.get_aliClass() + "\n");
				if(cnp.get_goodSynset() != null) {
					printer.print("Synset selecionado BabelNet: " + cnp.get_goodSynset().getSynset().toString() + "\n");
					printer.print("Lista de hiperonímios completa: " + cnp.get_utilities().getHypernyms() + "\n");
					printer.print("Hiperonímio selecionado: " + cnp.get_utilities().getSelected_hypernym() +
							" no nível de busca " + cnp.get_utilities().getLevel() + " e índice " +
							cnp.get_utilities().getIdx() + "\n");
					printer.print("Número de Synsets recuperados: " + cnp.get_utilities().get_numSy() + "\n\n");
					printer.print("Conjunto de synsets recuperados:\n");
					List<BabelNetResource.SearchObject>synsets = cnp.get_utilities().get_synsetCntx();
					for(BabelNetResource.SearchObject so : synsets){
						printer.print("\nSynset: " + so.getSynset() + "\n");
						printer.print("Sentidos: " + so.getSenses() + "\n");
						printer.print("Glosses: " + so.getGlosses() + "\n");
					}
				}
				else{
					printer.print("Não foi possível encontar synsets para esse conceito!");
				}
				int index = 0;
				for(BabelNetResource.SearchObject so : cnp.get_utilities().get_synsetCntx()) {
					List<String> cntxt = so.getBgw();
					printer.print(so.getSynset() + " | " + so.getGlosses() + "\n");
					printer.print("BOW: " + cntxt.toString() + "\n");
					printer.print("MEDIA: " + cnp.get_utilities().get_synsetMedia().get(index).toString());
					index++;	
					printer.print("\n\n");
				}
				printer.print("\n----------\n");
				
			}
		arq.close();
		} catch(IOException e) {
			System.out.println("Operação I/O interrompida, no arquivo de saída syCNTXT!");
	    	System.out.println("erro: " + e);
		}
	}

	/*
	public void out_file_we_wn_pair(List<Concept> listDomain) {
		try {
			FileWriter arq = new FileWriter(this.outPath);
			PrintWriter printer = new PrintWriter(arq);
			
			for(Concept cnp: listDomain) {
				printer.print("NOME: " + cnp.get_className() + "\n");
				printer.print("Desc: " + cnp.get_desc() + "\n");
				printer.print("Supers: " + cnp.get_supers() + "\n");
				printer.print("Subs: " + cnp.get_subs() + "\n");
				printer.print("Contexto: " + cnp.get_context() + "\n");
				printer.print("Conceito Topo alinhado: " + cnp.get_aliClass() + "\n");
				printer.print("Synset selecionado: " + cnp.get_goodSynset() + "\n");
				printer.print("Conjunto de synsets recuperados:\n");
				
				int index = 0;
				for(Entry<BabelSynset, LinkedHashMap<String, LinkedHashMap<String, Double>> > entry : cnp.get_utilities().get_pairSim().entrySet()) {
					LinkedHashMap<String, LinkedHashMap<String, Double>> value = entry.getValue();
					printer.print("\n" + entry.getKey() + "\n");
	Complmenting 				printer.print(String.format("%20s%16s\r\n", "ELEMENTO CONTEXTO|", "BAG OF WORDS"));
					
					for(Entry<String, LinkedHashMap<String, Double>> entry2: value.entrySet()) {
						LinkedHashMap<String, Double> value2 = entry2.getValue();
						printer.print(String.format("%20s", entry2.getKey() + "|"));
						
						for(Entry<String, Double> entry3: value2.entrySet()) {
							printer.print("    " + entry3.getKey() + ":" + entry3.getValue().floatValue() + ";");
						}
						printer.print("\n\n");
					}
					printer.print("MEDIA FINAL: " + cnp.get_utilities().get_synsetMedia().get(index).floatValue());
					printer.print("\n\n");
					index++;
				}
				printer.print("----------\n");
				
			}
		arq.close();
		} catch(IOException e) {
			System.out.println("Operação I/O interrompida, no arquivo de saída syCNTXTPair!");
	    	System.out.println("erro: " + e);
		}
	}
	
	public void out_file_we(List<Concept> listDomain) {
		try {
			FileWriter arq = new FileWriter(this.outPath);
			PrintWriter printer = new PrintWriter(arq);

			for (Concept cnp : listDomain) {
				printer.print("NOME: " + cnp.get_className() + "\n");
				printer.print("Desc: " + cnp.get_desc() + "\n");
				printer.print("Supers: " + cnp.get_supers() + "\n");
				printer.print("Subs: " + cnp.get_subs() + "\n");
				printer.print("Contexto: " + cnp.get_context() + "\n");
				printer.print("Conceito Topo alinhado: " + cnp.get_aliClass() + "\n");
				
				List<OutObjectWE> ooList = (List<OutObjectWE>) cnp.get_obj();
				for (OutObjectWE oo : ooList) {
					int aux = 0;
					Double[] vec = oo.get_vec();
					String name = oo.get_topConcept().get_className();
					printer.print("\nConceito Topo: " + name + "\n");
					printer.print(String.format("%20s%16s\r\n", "ELEMENTO CONTEXTO|", "BAG OF WORDS"));
					for (Entry<String, Object> entry : oo.get_map().entrySet()) {
						HashMap<String, Double> value = (HashMap<String, Double>) entry.getValue();
						
						printer.print(String.format("%20s", entry.getKey() + "=" + vec[aux] + "|"));
						for (Entry<String, Double> entry2 : value.entrySet()) {
							printer.print("    " + entry2.getKey() + ":" + entry2.getValue().floatValue() + ";");
						}
						printer.print("\n");
						aux++;
					}
					printer.print("\n");
					printer.print("MEDIA FINAL: " + oo.get_mediaTotal());
					printer.print("\n\n");
				}
				printer.print("----------\n");
			}
			arq.close();
		} catch (IOException e) {
			System.out.println("Operação I/O interrompida, no arquivo de saída syCNTXTPair!");
			System.out.println("erro: " + e);
		}
	}
	*/

}
