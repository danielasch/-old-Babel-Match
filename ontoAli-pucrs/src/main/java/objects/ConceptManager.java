package objects;

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import resources.BabelNetResource;
import resources.Utilities;

/**
 * This class is used to bring the object Concept to the local package
 */
public class ConceptManager {

//public methods

    /**
     *These methods calls protected methods from the Concept class.
     *This way, enabling modify the Concept class outside the Objects package.
     */
    public String getConceptNameWn(Concept cnp) {
        return cnp.spConceptName();
    }

    public void configOwlOntology(Concept cnp, OWLOntology onto) {
        cnp.setOwlOntology(onto);
    }

    public void configOwlClass(Concept cnp, OWLClass cls) {
        cnp.setOwlClass(cls);
    }

    public void configClassId(Concept cnp, String id) {
        cnp.setClassID(id);
    }

    public void configClassName(Concept cnp, String name) {
        cnp.setClassName(name);
    }

    public void configDescription(Concept cnp, String desc) {
        cnp.setDesc(desc);
    }

    public void configContext(Concept cnp, Set<String> cntxt) {
        cnp.setContext(cntxt);
    }

    public void configSupers(Concept cnp, List<OWLClassExpression> supers) {
        cnp.setSupers(supers);
    }

    public void configSubs(Concept cnp, List<OWLClassExpression> subs) {
        cnp.setSubs(subs);
    }

    public void configSynset(Concept cnp, BabelNetResource.SearchObject synset) {
        cnp.setGoodSynset(synset);
    }

    public void configAliClass(Concept cnp, OWLClass cls) {
        cnp.setAliClass(cls);
    }

    public void configUtilities(Concept cnp, Utilities ut) {
        cnp.set_utilities(ut);
    }

    public void configObject(Concept cnp, Object obj) {
        cnp.setObject(obj);
    }
}
