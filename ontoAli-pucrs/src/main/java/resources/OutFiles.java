package resources;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import objects.Concept;

/**
 * This class generates the text files
 */
public class OutFiles {
	
//Attributes
	
	//Contains path to write the text files
	private String outPath;


//Constructor	
	
	/**
	 * This constructor receive the path to the alignment and
	 * change .rdf by _1.text, this way, the text file will be
	 * generated at the save folder as the alignment
	 */
	public OutFiles(String path) {
		String aux = path.replace(".rdf", "_1.txt");
		this.outPath = aux;
	}


//Methods
	
	/**
	 * Generates the text file for the overlapping technique
	 */
	public void outFile(List<Concept> listDomain) {
		try {
			FileWriter arq = new FileWriter(this.outPath);
			PrintWriter printer = new PrintWriter(arq);
			List<String> bgwSelect;
			for(Concept cnp: listDomain) {
				printer.print("\n>Ontology Info.<\n");
				printer.print("Nome do conceito de domínio: " + cnp.getClassName() + "\n");
				printer.print("Descrição: " + cnp.getDesc() + "\n");
				printer.print("Supers: " + cnp.getSupers() + "\n");
				printer.print("Subs: " + cnp.getSubs() + "\n");
				printer.print("Contexto Domínio conceito: " + cnp.getContext() + "\n");
				printer.print("Conceito Topo alinhado: ");
				if(cnp.getAliClass() != null) {
					String top = cnp.getAliClass().toString();
					printer.print(top.substring(top.lastIndexOf("/")+1,top.length()-1) + "\n");
				}else{
					printer.print("Não foi possível realizar o alinhamento!\n");
				}
				printer.print("\n>BabelNet Info.<\n");
				printer.print("Synset selecionado BabelNet: ");
				if(cnp.getGoodSynset() != null) {
					printer.print(cnp.getGoodSynset().getSynset().toString() + "\n");
					if(cnp.getUtilities().getSelectedHypernym()!=null) {
						printer.print("Hiperonímio selecionado: " + cnp.getUtilities().getSelectedHypernym() +
								" no nível de busca " + cnp.getUtilities().getLevel() + "\n");
						printer.print("Caminho realizado: " + cnp.getUtilities().getHypernyms() + "\n");
					}else{
						printer.print("Hiperonímio selecionado: Não foi encontrado nenhum hiperonímio na ontologia de topo!\n");
						printer.print("Caminho realizado: Nenhum caminho encontrado!\n");
					}
					printer.print("Número de Synsets recuperados: " + cnp.getUtilities().getNumSy() + "\n\n");
					printer.print("Conjunto de synsets recuperados:\n");
					List<BabelNetResource.SearchObject> synsets = cnp.getUtilities().getSynsetCntx();
					int i = 1;
					for (BabelNetResource.SearchObject so : synsets) {
						printer.print("\n"+i+")\n");
						printer.print(">Synset: " + so.getSynset() + "\n");
						printer.print(">Sentidos: " + so.getSenses() + "\n");
						printer.print(">Glosses: " + so.getGlosses() + "\n");
						printer.print(">BOW: " + so.getBgw().toString() + "\n");
						i++;
					}
					printer.print("\n");
					bgwSelect = cnp.getGoodSynset().getBgw();
					printer.print("Intersecção de palavras encontrada - LESK:");
					for (String a : bgwSelect) {
						if (cnp.getContext().contains(a)) {
							printer.print(" " + a + " ");
						}
					}
					printer.print("\n-----------------------------------------------------------------------------------------------------\n");
					printer.print("-------------------------------------------New concept-----------------------------------------------\n");
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


	/**
	 * Generates the text file for the Word Embeddings technique
	 */
	public void outFileWeWn(List<Concept> listDomain) {
		try {
			FileWriter arq = new FileWriter(this.outPath);
			PrintWriter printer = new PrintWriter(arq);
			
			for(Concept cnp: listDomain) {
				printer.print("Nome do conceito de domínio: " + cnp.getClassName() + "\n");
				printer.print("Descrição: " + cnp.getDesc() + "\n");
				printer.print("Supers: " + cnp.getSupers() + "\n");
				printer.print("Subs: " + cnp.getSubs() + "\n");
				printer.print("Contexto: " + cnp.getContext() + "\n");
				printer.print("Conceito Topo alinhado: " + cnp.getAliClass() + "\n");
				if(cnp.getGoodSynset() != null) {
					printer.print("Synset selecionado BabelNet: " + cnp.getGoodSynset().getSynset().toString() + "\n");
					printer.print("Lista de hiperonímios completa: " + cnp.getUtilities().getHypernyms() + "\n");
					printer.print("Hiperonímio selecionado: " + cnp.getUtilities().getSelectedHypernym() +
							" no nível de busca " + cnp.getUtilities().getLevel() + " e índice " + "\n");
					printer.print("Número de Synsets recuperados: " + cnp.getUtilities().getNumSy() + "\n\n");
					printer.print("Conjunto de synsets recuperados:\n");
					List<BabelNetResource.SearchObject>synsets = cnp.getUtilities().getSynsetCntx();
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
				for(BabelNetResource.SearchObject so : cnp.getUtilities().getSynsetCntx()) {
					List<String> cntxt = so.getBgw();
					printer.print(so.getSynset() + " | " + so.getGlosses() + "\n");
					printer.print("BOW: " + cntxt.toString() + "\n");
					printer.print("MEDIA: " + cnp.getUtilities().getSynsetMedia().get(index).toString());
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
				printer.print("NOME: " + cnp.getClassName() + "\n");
				printer.print("Desc: " + cnp.getDesc() + "\n");
				printer.print("Supers: " + cnp.getSupers() + "\n");
				printer.print("Subs: " + cnp.getSubs() + "\n");
				printer.print("Contexto: " + cnp.getContext() + "\n");
				printer.print("Conceito Topo alinhado: " + cnp.getAliClass() + "\n");
				printer.print("Synset selecionado: " + cnp.getGoodSynset() + "\n");
				printer.print("Conjunto de synsets recuperados:\n");
				
				int index = 0;
				for(Entry<BabelSynset, LinkedHashMap<String, LinkedHashMap<String, Double>> > entry : cnp.getUtilities().getPairSim().entrySet()) {
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
					printer.print("MEDIA FINAL: " + cnp.getUtilities().getSynsetMedia().get(index).floatValue());
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
				printer.print("NOME: " + cnp.getClassName() + "\n");
				printer.print("Desc: " + cnp.getDesc() + "\n");
				printer.print("Supers: " + cnp.getSupers() + "\n");
				printer.print("Subs: " + cnp.getSubs() + "\n");
				printer.print("Contexto: " + cnp.getContext() + "\n");
				printer.print("Conceito Topo alinhado: " + cnp.getAliClass() + "\n");
				
				List<OutObjectWE> ooList = (List<OutObjectWE>) cnp.getObject();
				for (OutObjectWE oo : ooList) {
					int aux = 0;
					Double[] vec = oo.getVector();
					String name = oo.getTopConcept().getClassName();
					printer.print("\nConceito Topo: " + name + "\n");
					printer.print(String.format("%20s%16s\r\n", "ELEMENTO CONTEXTO|", "BAG OF WORDS"));
					for (Entry<String, Object> entry : oo.getMap().entrySet()) {
						HashMap<String, Double> value = (HashMap<String, Double>) entry.getValue();
						
						printer.print(String.format("%20s", entry.getKey() + "=" + vec[aux] + "|"));
						for (Entry<String, Double> entry2 : value.entrySet()) {
							printer.print("    " + entry2.getKey() + ":" + entry2.getValue().floatValue() + ";");
						}
						printer.print("\n");
						aux++;
					}
					printer.print("\n");
					printer.print("MEDIA FINAL: " + oo.getTotalAverage());
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
