package objects;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import it.uniroma1.lcl.babelnet.BabelSynset;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import edu.mit.jwi.item.ISynset;
import resources.BabelNetResource;
import resources.Utilities;

/**
 * Concept class contains all the information about a OWLClass
 */
public class Concept {

//Attributes 	
	
	//OWLOntology
	private OWLOntology ontology;
	//OWLClass
	private OWLClass owlClass;
	//OWLOntology ID
	private String ontologyID;
	//OWLClass ID
	private String classID;
	//Ontology name
	private String ontologyName;
	//Class name
	private String className;
	//Concept annotation
	private String desc;
	//Concept context
	private Set<String> context;					
	// Auxiliary class to save the information about this concept 
	//to generate the text out file.
	private Utilities ut;
	private Object obj;
	//Super concepts list
	private List<OWLClassExpression> supers;
	//Sub concepts list
	private List<OWLClassExpression> subs;
	//Synset disambiguated
	private BabelNetResource.SearchObject goodSynset;
	//OWLClass aligned to this concept
	private OWLClass aliClass;


//Constructor
	
	public Concept() {
		this.context = new HashSet<String>();
		this.goodSynset = null;
		this.ut = null;
	}


//Getters
	
	public OWLOntology getOwlOntology() {
		return this.ontology;
	}

	public OWLClass get_owlClass() {
		return owlClass;
	}

	String getOntologyID() {
		return ontologyID;
	}

	public String getClassID() {
		return classID;
	}

	public String getOntologyName() {
		return ontologyName;
	}

	public String getClassName() {
		return className;
	}

	public String getDesc() {
		return desc;
	}

	public Set<String> getContext() {
		return context;
	}

    public Utilities getUtilities() {
        return this.ut;
    }

    public List<OWLClassExpression> getSupers() {
        return supers;
    }

    public List<OWLClassExpression> getSubs() {
        return subs;
    }

    public BabelNetResource.SearchObject getGoodSynset() {
        return goodSynset;
    }

    public OWLClass getAliClass() {
        return aliClass;
    }

    public Object getObject() {
        return obj;
    } //this changed to used instead of unused when i've refactored it <<<


//Setters

    protected void setOwlOntology(OWLOntology onto) {
        this.ontology = onto;
    } //HERE

    protected void setOwlClass(OWLClass owlclass) { owlClass = owlclass; }

    void setOntologyID(String _ontologyID) {
        ontologyID = _ontologyID;
    }

    void setClassID(String _classID) {
        classID = _classID;
    }

    void setOntologyName(String _ontologyName) {
        ontologyName = _ontologyName;
    }

    void setClassName(String _className) {
        className = _className;
    }

    protected void setContext(Set<String> set) {
        context = set;
    }

    protected void setDesc(String _desc) {
        desc = _desc;
    }

    protected void set_utilities(Utilities ut) {
        this.ut = ut;
    }

	protected void setSupers(List<OWLClassExpression> _supers) {
		supers = _supers;
	}

	protected void setSubs(List<OWLClassExpression> _subs) {
		subs = _subs;
	}

	void setGoodSynset(BabelNetResource.SearchObject _goodSynset) { goodSynset = _goodSynset; }

	void setAliClass(OWLClass _aliclass) {
		aliClass = _aliclass;
	}

	void setObject(Object _obj) {
		obj = _obj;
	}


//Methods

    /**
    *Prints the concept's information
    */
    public void printInfo() {
		
		System.out.println("Concept: " + this.className);
		System.out.println("Description: " + this.desc);

		if(this.context != null) {
			String out = "Context: ";
			Iterator<String> iterator = this.context.iterator();
			while(iterator.hasNext()) {
				String a = iterator.next();
				if(!iterator.hasNext()) {
					out = out + a + ".";
				} else {
					out = out + a + ", "; 
				}
			}
			System.out.println(out + "\n");
		} else {
			System.out.println("Context: null" + "\n");
		}
		
		if(this.goodSynset != null) {
			System.out.println("Synset: " + this.goodSynset.getSynset().toString());
			System.out.println("Gloss: " + this.goodSynset.getGlosses() + "\n");
		} else {
			System.out.println("Synset: null" + "\n");
		}
	}

	
	/**
	 * This method identify if the concept name is separated by under line, or hyphen, or UpperCase or 
	 *if the concept name is simple.
	 * Then separate the concept name, returning the last token or the simple term.
	 */
	protected String spConceptName() {
		String name = null;
		String cnpName = this.className;
		if(cnpName.contains("_")) {
			String words[];
			words = cnpName.split("_");
			int i = words.length;
			name = words[i - 1];	
		} else if(cnpName.contains("-")) {
			String words[];
			words = cnpName.split("-");
			int i = words.length;
			name = words[i - 1];
		} else if(hasUpperCase(cnpName)) {
			int x = cnpName.length();
			int up = 0;
			for(int y = 1; y < x; y++) {
				if(Character.isUpperCase(cnpName.charAt(y)) && y > up) {
					up = y;	
				}	
			}
			if(up != 0) {
				name = cnpName.substring(up);
			}
		} else {
			name = cnpName;
		}
		return name;
	}	


	/**
	 * This methods test if a string has UpperCase in its middle.
	 */
	private boolean hasUpperCase(String word) {
		
		int x = word.length();
		
		for(int y = 1; y < x; y++) {
			if(Character.isUpperCase(word.charAt(y))) {
				return true;
			}	
		}
		return false;	
	}
	
}
