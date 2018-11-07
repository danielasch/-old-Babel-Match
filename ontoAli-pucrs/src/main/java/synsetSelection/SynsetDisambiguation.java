package synsetSelection;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import objects.Concept;
import objects.ConceptManager;
import resources.BabelNetResource;
import resources.BaseResource;
import resources.StanfordLemmatizer;
import resources.Utilities;

/**
 * This class disambiguate the recovered synsets for a concept
 */
public class SynsetDisambiguation {

//Attributes

    //BaseResource contains the necessary resources to execute the disambiguation
    private BaseResource base;
    private BabelNetResource bn;


//Constructor

    public SynsetDisambiguation(BaseResource _base) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Synset didambiguation selected!");
        this.base = _base;
        this.bn = new BabelNetResource();
    }


//Log Methods

    private void initLog() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Disambiguating Synsets...");
    }

    private void finalLog() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Synsets disambiguated!");
    }


//Methods

    /**
     * This method selects the right synset to a concept
     */
    public void disambiguation(List<Concept> listCon) {
        try {
            initLog();
            for (Concept concept : listCon) {
                rcGoodSynset(concept);
            }
            finalLog();
        } catch (IOException e) {
            System.out.println("I/O operation failed - WN dictinoary!");
            System.out.println("error: " + e);
        }
    }


    /**
     * Disambiguation process
     */
    public void rcGoodSynset(Concept concept) throws IOException {
        StanfordLemmatizer slem = base.getLemmatizer();
        ConceptManager man = new ConceptManager();
        Utilities ut = new Utilities();
        List<String> context = slem.toList(concept.getContext());
        String name = man.getConceptNameWn(concept);
        List<String> cnpNameLemma = slem.lemmatize(name);

        int i = cnpNameLemma.size();
        name = cnpNameLemma.get(i - 1);
        int numSy = 0;
        BabelNetResource.SearchObject bestSynset = null;
        List<BabelNetResource.SearchObject> searched = bn.search(name);

        if (searched.size() != 1) {
            bestSynset = leskTechnique(searched, context);
            man.configSynset(concept, bestSynset);
            ut.setNumSy(searched.size()-1);

        } else {
            BabelNetResource.SearchObject synset = searched.get(0);
            man.configSynset(concept, synset);
            ut.setNumSy(1);
        }

        ut.setSynsetCntx(searched);
        man.configUtilities(concept, ut);
    }


    /**
     * Overlapping between two lists
     */
    public int intersection(List<String> bagSynset,List<String> context) {
        int inter = 0;
        for (String word : context) {
            word = word.toLowerCase();
            for (String wordCompared : bagSynset) {
                if (word.equals(wordCompared)) {
                    inter++;
                    break;
                }
            }
        }
        return inter;
    }


    public BabelNetResource.SearchObject leskTechnique(List<BabelNetResource.SearchObject>context_1,
                                                       List<String>context_2){
        BabelNetResource.SearchObject selected = null;
        int max = -1;
        for(BabelNetResource.SearchObject so : context_1) {
            int test = intersection(so.getBgw(), context_2);
            if (test > max) {
                selected = so;
                max = test;
            }
        }
        return selected;
    }


    /**
     * Remove some char of a string
     */
	private String rmSpecialChar(String word) {
		if(word.contains("(")) {
        	word = word.replace("(", "");
        }
        if(word.contains(")")) {
        	word = word.replace(")", "");
        }
        if(word.contains(",")) {
        	word = word.replace(",", "");
        }
        if(word.contains(":")) {
        	word = word.replace(":", "");
        }
        if(word.contains("'")) {
        	word = word.replace("'", "");
        }
        if(word.contains(".")) {
        	word = word.replace(".", "");
        }
        if(word.contains("?")) {
        	word = word.replace("?","");
        }
        if(word.contains("!")) {
        	word = word.replace("!","");
        }
        return word;
	}

}
