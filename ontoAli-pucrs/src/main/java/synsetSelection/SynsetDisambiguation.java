package synsetSelection;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

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
        //The lemmatizer
        StanfordLemmatizer slem = base.get_lemmatizer();
        ConceptManager man = new ConceptManager();
        //Utilities carries the temp1 list and the numSy of a concept
        Utilities ut = new Utilities();
        List<String> context = slem.toList(concept.get_context());
        //name receive the concept name
        String name = man.conceptName_wn(concept);
        //lemmatize the concept name
        List<String> cnpNameLemma = slem.lemmatize(name);
        int i = cnpNameLemma.size();
        //name receive the concif(idxWord != null) {ept name lemmatized
        name = cnpNameLemma.get(i - 1);
        //numSy will receive the number of synsets recovered to a concept (OutFiles use only)
        int numSy = 0;
        BabelNetResource.SearchObject bestSynset = null;
        //System.out.println(name);
        List<BabelNetResource.SearchObject> searched = bn.search(name);
        if (searched.size() != 1) {
            int max = 0;
            for (BabelNetResource.SearchObject s : searched) {
                //System.out.println(s.getSynset().getMainSense());
                //size receive the number of overlaps between two lists
                //size of the intersection between the context of a concept and
                //it's recovered synset's bag of words
                int size = intersection(context, s.getBgw());
                //if the intersection value it's greater than the older ones
                //it must be set as the best recovered synset of a given concept
                if (size > max) {
                    max = size;
                    //sets the best synset of a given concept
                    bestSynset = s;
                }
                numSy++;
            }
            man.config_synset(concept, bestSynset);
            //setting the number os synsets using utilities class
            ut.set_numSy(numSy);
        } else {
            BabelNetResource.SearchObject synset = searched.get(0);
            man.config_synset(concept, synset);
            //utilities set the number os synsets,
            //in this case 1
            ut.set_numSy(1);
        }
        //utilities sets the synset and the bag of words map
        //>>>>>>>ut.set_synsetCntx(temp1);
        //sets the utilities of a concept
        ut.set_synsetCntx(searched);
        man.config_utilities(concept, ut);
    }

    /*
     * create the bag of words of a synset
     */
	/*
	private List<String> createBagWords(List<IWord> wordsSynset, String glossSynset) {
	    List<String> list = new ArrayList<String>();
	    Set<String> set = new HashSet<String>();
	    StanfordLemmatizer slem = this.base.get_lemmatizer();
	    for (IWord i : wordsSynset) {
	    	StringTokenizer st = new StringTokenizer(i.getLemma().toLowerCase().replace("_"," ")," ");
	    	while (st.hasMoreTokens()) {
	    		  String token = st.nextToken();
	    	 	  if (!list.contains(token)) {
	    	  	      list.add(token);
	    	      }
	    	}
	    }
	    glossSynset = glossSynset.replaceAll(";"," ").replaceAll("\"", " ").replaceAll("-"," ").toLowerCase();
	    StringTokenizer st = new StringTokenizer(glossSynset," ");
    	while (st.hasMoreTokens()) {
    		   String token = st.nextToken().toLowerCase();
    		   token = rm_specialChar(token);
    		   if (!this.base.get_StpWords().contains(token) && !list.contains(token)) {
    			   list.add(token);
    		   }
    	}
    	//turn the list into a string to lemmatize the list
    	String toLemma = slem.toLemmatize(list);
    	//clears the list
		list.clear();
		//list receive the string lemmatized
		list = slem.lemmatize(toLemma);
		//turns the list into a set,
		//to avoid repeated lemmatized strings
		set =  slem.toSet(list);
		//turns back the set into a list
		list = slem.toList(set);
	   return list;
	}
	*/


    /*
     * Overlapping between two lists
     */
    int intersection(List<String> context, List<String> bagSynset) {
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

    /*
     * Remove some char of a string
     */
	/*
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
	*/


}
