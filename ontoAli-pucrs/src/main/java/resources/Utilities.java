package resources;

import java.util.*;

import edu.mit.jwi.item.ISynset;
import it.uniroma1.lcl.babelnet.BabelSynset;

/**
 * This class saves the information (about a concept) used to generate the text files.
 */
public class Utilities {

//Attributes
	
	//This map saves the retrieved synsets and its bag of words, for a concept
	//private Map<BabelSynset, List<String>> synsetCntxt;
	private List<BabelNetResource.SearchObject> synsetCntxt;
	//The number of synsets retrieved for a concept
	private int numSy, level;
	//This list saves the averages between the context and the bag of words of a synset
	//Only used on the Word Embeddigs technique
	private List<Double> listMedia;
	private LinkedHashMap<BabelSynset, LinkedHashMap<String, LinkedHashMap<String, Double> > > mapPairSim;
    private String hypernyms;
    private String selected_hypernym;


//Constructor	
	
	public Utilities() {
		this.synsetCntxt = null;
		this.numSy = level = 0;
		this.listMedia = null;
		this.hypernyms = null;
		this.selected_hypernym = null;
		//this.mapPairSim = null;
	}


//Getters

    //public void setSynsetCntx(HashMap<BabelSynset, List<String>> _synsetCntxt) { this.synsetCntxt = _synsetCntxt; }

    public List<BabelNetResource.SearchObject> getSynsetCntx() { return synsetCntxt; }

	public int getNumSy() {
		return numSy;
	}

	public String getHypernyms(){ return this.hypernyms; }

    public String getSelectedHypernym() { return selected_hypernym; }

    public int getLevel() { return level; }

    public List<Double> getSynsetMedia() { return listMedia; }

    public LinkedHashMap<BabelSynset,LinkedHashMap<String,LinkedHashMap<String,Double>>> getPairSim() { return mapPairSim; }


//Setters

    public void setSynsetCntx(List<BabelNetResource.SearchObject> synsetCntxt){ this.synsetCntxt = synsetCntxt; }

    public void setNumSy(int num) {
        this.numSy = num;
    }

    public void setHypernyms(String hypernyms) { this.hypernyms = hypernyms; }

    public void setSelectedHypernym(String selected_hypernym) { this.selected_hypernym = selected_hypernym; }

    public void setLevel(int level) { this.level = level; }

    public void setSynsetMedia(List<Double> _synsetMedia) { this.listMedia = _synsetMedia; }

	public void setPairSim(LinkedHashMap<BabelSynset, LinkedHashMap<String, LinkedHashMap<String, Double> > > _mapPairSim) {
		this.mapPairSim = _mapPairSim;
	}

}
