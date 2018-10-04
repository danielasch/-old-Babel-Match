package resources;

import java.util.*;

import edu.mit.jwi.item.ISynset;
import it.uniroma1.lcl.babelnet.BabelSynset;

/*
 * This class saves the information (about a concept) used to generate the text files.
 */
public class Utilities {

//Attributes
	
	//This map saves the retrieved synsets and its bag of words, for a concept
	//private Map<BabelSynset, List<String>> synsetCntxt;
	private List<BabelNetResource.SearchObject> synsetCntxt;
	//The number of synsets retrieved for a concept
	private int numSy, level, idx;
	//This list saves the averages between the context and the bag of words of a synset
	//Only used on the Word Embeddigs technique
	private List<Double> listMedia;
	private LinkedHashMap<BabelSynset, LinkedHashMap<String, LinkedHashMap<String, Double> > > mapPairSim;
    private String hypernyms;
    private String selected_hypernym;


//Constructor	
	
	public Utilities() {
		this.synsetCntxt = null;
		this.numSy = level = idx = 0;
		this.listMedia = null;
		this.hypernyms = null;
		this.selected_hypernym = null;
		//this.mapPairSim = null;
	}

//Getters and setters

    //public void set_synsetCntx(HashMap<BabelSynset, List<String>> _synsetCntxt) { this.synsetCntxt = _synsetCntxt; }

    public void set_synsetCntx(List<BabelNetResource.SearchObject> synsetCntxt){ this.synsetCntxt = synsetCntxt; }

    public List<BabelNetResource.SearchObject> get_synsetCntx() { return synsetCntxt; }

	public void set_numSy(int num) {
		this.numSy = num;
	}

	public int get_numSy() {
		return numSy;
	}

    public void set_hypernyms(String hypernyms) { this.hypernyms = hypernyms; }

	public String getHypernyms(){ return this.hypernyms; }

    public String getSelected_hypernym() { return selected_hypernym; }

    public void setSelected_hypernym(String selected_hypernym) { this.selected_hypernym = selected_hypernym; }

    public int getLevel() { return level; }

    public void setLevel(int level) { this.level = level; }

    public int getIdx() { return idx; }

    public void setIdx(int idx) { this.idx = idx; }

    public void set_synsetMedia(List<Double> _synsetMedia) { this.listMedia = _synsetMedia; }
	
	public List<Double> get_synsetMedia() { return listMedia; }
	
	public void set_pairSim(LinkedHashMap<BabelSynset, LinkedHashMap<String, LinkedHashMap<String, Double> > > _mapPairSim) {
		this.mapPairSim = _mapPairSim;
	}
	
	public LinkedHashMap<BabelSynset, LinkedHashMap<String, LinkedHashMap<String, Double> > > get_pairSim() { return mapPairSim; }

}
