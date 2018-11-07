package synsetSelection;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import edu.mit.jwi.item.IWord;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.jlt.util.Language;
import objects.Concept;
import objects.ConceptManager;
import resources.BabelNetResource;
import resources.BaseResource;
import resources.StanfordLemmatizer;
import resources.Utilities;

/*
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

    private void init_log() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Disambiguating Synsets...");
    }

    private void final_log() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Synsets disambiguated!");
    }

//Methods

    /*
     * This method selects the right synset to a concept
     */
    public void disambiguation(List<Concept> listCon) {
        try {
            init_log();
            for (Concept concept : listCon) {
                rc_goodSynset(concept);
            }
            final_log();
        } catch (IOException e) {
            System.out.println("I/O operation failed - WN dictinoary!");
            System.out.println("error: " + e);
        }
    }

    /*
     * Disambiguation process
     */
    public void rc_goodSynset(Concept concept) throws IOException {
        StanfordLemmatizer slem = base.get_lemmatizer();
        ConceptManager man = new ConceptManager();
        Utilities ut = new Utilities();
        List<String> context = slem.toList(concept.get_context());
        String name = man.conceptName_wn(concept);
        List<String> cnpNameLemma = slem.lemmatize(name);

        int i = cnpNameLemma.size();
        name = cnpNameLemma.get(i - 1);
        int numSy = 0;
        BabelNetResource.SearchObject bestSynset = null;
        List<BabelNetResource.SearchObject> searched = bn.search(name);

        if (searched.size() != 1) {
            bestSynset = leskTechnique(searched, context);
            man.config_synset(concept, bestSynset);
            ut.set_numSy(searched.size()-1);

        } else {
            BabelNetResource.SearchObject synset = searched.get(0);
            man.config_synset(concept, synset);
            ut.set_numSy(1);
        }

        ut.set_synsetCntx(searched);
        man.config_utilities(concept, ut);
    }

    /*
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

    /*
     * Remove some char of a string
     */

	private String rm_specialChar(String word) {
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
