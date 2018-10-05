package resources;


import com.babelscape.util.UniversalPOS;
import conceptExtraction.ContextProcessing;
import it.uniroma1.lcl.babelnet.*;
import it.uniroma1.lcl.babelnet.data.BabelGloss;
import it.uniroma1.lcl.babelnet.data.BabelPointer;
import it.uniroma1.lcl.jlt.util.Language;

import java.util.*;

//BabelNet resource class

public class BabelNetResource {

    /*Public object related to babelnet's search
    *which keeps the senses, glosses and context of a given babelsynset
    */
    public class SearchObject {

        private ArrayList<String> senses;
        private ArrayList<String> glosses;
        private List<String> bgw;
        private BabelSynset bs;

        public SearchObject(List<BabelSense> senses, List<BabelGloss> glosses, BabelSynset bs) {
            this.senses = lemmatizeSenses(senses.toString());
            this.glosses = lemmatizeGloss(glosses.toString());
            setBgw(babelBow(this.senses, this.glosses));
            this.bs = bs;
        }

        //Getters
        public ArrayList<String> getSenses() {
            return senses;
        }

        public ArrayList<String> getGlosses() { return glosses; }

        public void setBgw(List<String> bgw){ this.bgw = bgw; }

        public List<String> getBgw() {
            return bgw;
        }

        public BabelSynset getSynset() {
            return bs;
        }
    }

    private BabelNet bn;
    private ContextProcessing cp;
    private BaseResource bs;

    public BabelNetResource() {
        bn = BabelNet.getInstance();
        bs = new BaseResource();
        cp = new ContextProcessing(bs);
    }

    /*Babelnet's search method (returns a list
     *of babelsynsets, senses and glosses for a given string target)
     */
    public List<SearchObject> search(String target) {
        //List of SearchObject objects which represents the aggregation of recovered synsets,
        // glosses and words that composes the synset's definition
        List<BabelNetResource.SearchObject> searchedObjects = new LinkedList<>();
        List<BabelSynset> bsl = getSynset(target);
        for (BabelSynset bs : bsl) {
            if (bs != null) {
                List<BabelSense> senses = bs.getSenses(Language.EN);
                List<BabelGloss> glosses = bs.getGlosses(Language.EN);
                SearchObject so = new SearchObject(senses, glosses, bs);
                searchedObjects.add(so);
                senses.clear();
                glosses.clear();
                senses = null;
                glosses = null;
            }
        }
        return searchedObjects;
    }

    //Search complement methods
    public List<BabelSynset> getSynset(String target) {
        BabelNetQuery query = new BabelNetQuery.Builder(target).from(Language.EN).POS(UniversalPOS.NOUN).build();
        return bn.getSynsets(query);
    }


    public List<BabelSynset> getHypernyms(BabelSynset bs, List<BabelSynset> hypernyms) {
        System.out.println("\nget hyp method ("+bs+")");
        List<BabelSynsetRelation> lbsr = bs.getOutgoingEdges(BabelPointer.ANY_HYPERNYM);
        if(lbsr == null || lbsr.size() == 0) return hypernyms;
        for (BabelSynsetRelation bsr : lbsr) {
            BabelSynsetID bsid = bsr.getBabelSynsetIDTarget();
            BabelSynset basy = bn.getSynset(bsid);
            hypernyms.add(basy);
            System.out.println("added " + basy.getMainSense() + " to hyp");
        }
        lbsr.clear();
        lbsr = null;
        return hypernyms;
    }

    /*
    public List<BabelSynset> getHypernyms(BabelSynset synset){
        List<BabelSynset> hypernyms = new LinkedList<>();
        List<BabelSynsetRelation> lbsr = synset.getOutgoingEdges(BabelPointer.ANY_HYPERNYM);
        for (BabelSynsetRelation bsr : lbsr) {
            BabelSynsetID bsid = bsr.getBabelSynsetIDTarget();
            BabelSynset basy = bn.getSynset(bsid);
            if(!hypernyms.contains(basy)) {
                hypernyms.add(basy);
            }
        }
        return hypernyms;
    }
    */

    //Lemmatizers
    public String lemmatizeHypernym(String hypernym) {
        ArrayList<String> ans = new ArrayList<>(1);
        ans.add(hypernym);
        if (hypernym.contains("#n")) {
            ans.set(0, hypernym.substring(0, hypernym.indexOf("#")));
        }
        if (hypernym.contains(":") && hypernym.contains("]")) {
            ans.set(0, hypernym.substring(hypernym.lastIndexOf(":") + 1, hypernym.indexOf("]")));
        }
        if (hypernym.contains(":")) {
            ans.set(0, hypernym.substring(hypernym.lastIndexOf(":") + 1));
        }
        String r = ans.get(0);
        ans.clear();
        return r;
    }

    public ArrayList<String> lemmatizeSenses(String senses) {
        String shortSense = senses.substring(1, senses.length() - 1);
        ArrayList<String> lemmaSense = transferArray(shortSense.split(", "));
        for (int i = 0; i < lemmaSense.size(); i++) {
            if (lemmaSense.get(i).equals("&")) {
                lemmaSense.remove(i);
                i--;
            }
            if (lemmaSense.get(i).contains("、")) {
                String g = lemmaSense.get(i).replaceAll("、", "");
                lemmaSense.set(i, g);
            }
            if (lemmaSense.get(i).contains(",")) {
                String g = lemmaSense.get(i).replaceAll(",", "");
                lemmaSense.set(i, g);
            }
            if (lemmaSense.get(i).contains("-")) {
                String g = lemmaSense.get(i).replaceAll("-", "");
                lemmaSense.set(i, g);
            }
            if (lemmaSense.get(i).contains(".")) {
                String g = lemmaSense.get(i).replaceAll(".", "");
                lemmaSense.set(i, g);
            }
            if (lemmaSense.get(i).contains(":")) {
                lemmaSense.set(i, lemmaSense.get(i).substring(lemmaSense.get(i).lastIndexOf(":") + 1));
            }
            if (lemmaSense.get(i).contains("(")) {
                lemmaSense.set(i, lemmaSense.get(i).replaceAll("[()]", ""));
            }
            if (isInteger(lemmaSense.get(i)) || lemmaSense.get(i).contains("_")) {
                if (isInteger(lemmaSense.get(i))) {
                    lemmaSense.remove(i);
                }
                else if (lemmaSense.get(i).contains("_")) {
                    String[] g = lemmaSense.get(i).split("_");
                    lemmaSense.remove(i);
                    for (String s : g) {
                        lemmaSense.add(s);
                    }
                }
                i--;
            }
        }
        return transferSet(lemmaSense);
    }

    public ArrayList<String> lemmatizeGloss(String gloss) {
        String shortGloss = gloss.substring(1, gloss.length() - 1);
        ArrayList<String> lemmaGloss = transferArray(shortGloss.split(" "));
        for (int i = 0; i < lemmaGloss.size(); i++) {
            if (lemmaGloss.get(i).contains(",")) {
                String g = lemmaGloss.get(i).replace(",", "");
                lemmaGloss.set(i, g);
            }
            if (lemmaGloss.get(i).contains(".")) {
                String g = lemmaGloss.get(i).replaceAll(".", "");
                lemmaGloss.set(i, g);
            }
            if (lemmaGloss.get(i).contains("(")) {
                String g = lemmaGloss.get(i).replaceAll("[()]", "");
                lemmaGloss.set(i, g);
            }
            if (lemmaGloss.get(i).contains((";"))) {
                String g = lemmaGloss.get(i).replaceAll(";", "");
                lemmaGloss.set(i, g);
            }
            if (lemmaGloss.get(i).contains((":"))) {
                String g = lemmaGloss.get(i).replaceAll(":", "");
                lemmaGloss.set(i, g);
            }
            if (lemmaGloss.get(i).equals("—")) {
                lemmaGloss.remove(i);
                i--;
            } else if (isInteger(lemmaGloss.get(i))) {
                lemmaGloss.remove(i);
                i--;
            } else if (lemmaGloss.get(i).contains("-") || lemmaGloss.get(i).contains("_")) {
                String[] g = null;
                if (lemmaGloss.get(i).contains("-")) {
                    g = lemmaGloss.get(i).split("-");
                } else if (lemmaGloss.get(i).contains("_")) {
                    g = lemmaGloss.get(i).split("_");
                }
                lemmaGloss.remove(i);
                for (String s : g) {
                    lemmaGloss.add(s);
                }
                i--;
            }
        }
        return transferSet(lemmaGloss);
    }

    //Auxiliary methods for string treatment
    public ArrayList<String> transferArray(String[] array) {
        if (array == null) return null;
        ArrayList<String> newArray = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            newArray.add(array[i].toLowerCase());
        }
        array = null;
        return newArray;
    }

    public ArrayList<String> transferSet(ArrayList<String> array) {
        Set<String> set = new HashSet<>();
        for (String s : array) {
            set.add(s);
        }
        array.clear();
        set = this.cp.rm_stopWords(set);
        for (String s : set) {
            array.add(s);
        }
        set.clear();
        return array;
    }

    public boolean isInteger(String value) {
        try {
            int k = Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> babelBow(List<String> senses, List<String> glosses) {
        List<String> bow = senses;
        for (String s : glosses) {
            if (!bow.contains(s)) bow.add(s);
        }
        return bow;
    }

}