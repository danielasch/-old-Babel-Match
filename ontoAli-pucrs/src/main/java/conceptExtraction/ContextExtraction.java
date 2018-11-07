package conceptExtraction;

import java.text.SimpleDateFormat;
import java.util.*;


import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import objects.Concept;
import objects.ConceptManager;

/**
 * This class extract the information about a concept from the ontology
 */
public class ContextExtraction {

//Constructor

    ContextExtraction() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Context method selected!");
    }

//Log methods

    private void initLog() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Extracting domain ontology classes...");
    }

    private void finalLog() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Domain ontology classes Extracted!");
    }

    private void initLogUpper() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Extracting upper ontology classes...");
    }

    private void finalLogUpper() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Upper ontology classes Extracted!");
    }

//Methods

    /**
     * Extracts the concept and its information from domain ontology
     */
    protected List<Concept> extract(OWLOntology onto) {
        initLog();
        //the list with all concepts extracted
        List<Concept> listCon = new ArrayList<Concept>();
        ConceptManager man = new ConceptManager();
        for (OWLClass owlClass : onto.getClassesInSignature()) {
            if (!owlClass.isTopEntity()) {

                //instantiate the Concept class
                Concept concept = new Concept();
                Set<String> context = new HashSet<String>();
                String desc = null;
                List<OWLClassExpression> listSup = new ArrayList<OWLClassExpression>();
                List<OWLClassExpression> listSub = new ArrayList<OWLClassExpression>();

                //extracts the super-owlclasses
                extractSuperClass(onto, owlClass, context, listSup);

                if (listSup.isEmpty() || verifyThing(listSup)) {
                    //sets the ontology into the concept class
                    man.configOwlOntology(concept, onto);
                    //sets the owlclass into the concept class
                    man.configOwlClass(concept, owlClass);
                    //sets the owlclassID into the concept class
                    man.configClassId(concept, owlClass.toString());
                    //sets the owlclass name into the concept class
                    man.configClassName(concept, owlClass.getIRI().getFragment());

                    //adds the concept name into the context
                    context.add(owlClass.getIRI().getFragment());
                    //desc receive the annotation of a concept
                    desc = extractAnnotation(onto, owlClass, context);
                    //sets the description into the concept class
                    man.configDescription(concept, desc);
                    //extracts the super-owlclasses
                    //extractSuperClass(onto, owlClass, context, listSup);
                    //extracts the sub-owlclasses
                    extractSubClass(onto, owlClass, context, listSub);
                    //sets the context list into a cocnept class
                    man.configContext(concept, context);
                    //sets the super-owlclasses list into a concept class
                    man.configSupers(concept, listSup);
                    //sets the sub-owlclasses list into a concept class
                    man.configSubs(concept, listSub);
                    //adds the Concept into a list
                    listCon.add(concept);
                }
            }
        }
        finalLog();
        return listCon;
    }


    protected List<Concept> extractWithContext(OWLOntology onto) {
        initLog();
        //the list with all concepts extracted
        List<Concept> listCon = new ArrayList<Concept>();
        ConceptManager man = new ConceptManager();
        Set<String> ontology_context = new HashSet<>();
        for (OWLClass owlClass : onto.getClassesInSignature()) {
            if (!owlClass.isTopEntity()) {
                //Adding the name of the current concept to the whole ontology context
                ontology_context.add(owlClass.getIRI().getFragment());
                //instantiate the Concept class
                Concept concept = new Concept();
                Set<String> context = new HashSet<String>();
                String desc = null;
                List<OWLClassExpression> listSup = new ArrayList<OWLClassExpression>();
                List<OWLClassExpression> listSub = new ArrayList<OWLClassExpression>();

                //extracts the super-owlclasses
                extractSuperClass(onto, owlClass, null, listSup);

                if (listSup.isEmpty() || verifyThing(listSup)) {
                    //sets the ontology into the concept class
                    man.configOwlOntology(concept, onto);
                    //sets the owlclass into the concept class
                    man.configOwlClass(concept, owlClass);
                    //sets the owlclassID into the concept class
                    man.configClassId(concept, owlClass.toString());
                    //sets the owlclass name into the concept class
                    man.configClassName(concept, owlClass.getIRI().getFragment());
                    //desc receive the annotation of a concept
                    desc = extractAnnotation(onto, owlClass, context);
                    //sets the description into the concept class
                    man.configDescription(concept, desc);
                    //extracts the super-owlclasses
                    //extractSuperClass(onto, owlClass, context, listSup);
                    //extracts the sub-owlclasses
                    //extractSubClass(onto, owlClass, context, listSub);
                    //sets the context list into a cocnept class
                    man.configContext(concept, context);
                    //sets the super-owlclasses list into a concept class
                    man.configSupers(concept, listSup);
                    //sets the sub-owlclasses list into a concept class
                    man.configSubs(concept, listSub);
                    //adds the Concept into a list
                    listCon.add(concept);
                }
            }
        }
        for (Concept c : listCon) {
            c.getContext().addAll(ontology_context);
        }
        finalLog();
        return listCon;
    }


    /**
     * Extracts the concept and its information from top ontology
     */
    protected List<Concept> extractUpper(OWLOntology onto) {
        initLogUpper();
        List<Concept> listCon = new ArrayList<Concept>();
        ConceptManager man = new ConceptManager();

        System.out.println("Upper Ontology: ");

        for (OWLClass owlClass : onto.getClassesInSignature()) {
            //instantiate the Concept class
            Concept concept = new Concept();
            String desc = null;
            //sets the ontology into the concept class
            man.configOwlOntology(concept, onto);
            //sets the owlclass into the concept class
            man.configOwlClass(concept, owlClass);
            //sets the owlclassID into the concept class
            man.configClassId(concept, owlClass.toString());
            //sets the owlclass name into the concept class
            man.configClassName(concept, owlClass.getIRI().getFragment());
            //desc receive the annotation of a concept
            desc = extractAnnotationUpper(onto, owlClass);
            //sets the description into the concept class
            man.configDescription(concept, desc);
            //adds the concept into a list
            listCon.add(concept);
        }
        finalLogUpper();
        return listCon;
    }


    protected List<Concept> extractUpperWE(OWLOntology onto) {
        initLogUpper();
        List<Concept> listCon = new ArrayList<Concept>();
        ConceptManager man = new ConceptManager();
        List<OWLClassExpression> listSub = new ArrayList<OWLClassExpression>();

        for (OWLClass owlClass : onto.getClassesInSignature()) {
            if (listSub.isEmpty() && !owlClass.isOWLThing()) {
                extractSubClass(onto, owlClass, null, listSub);
                // instantiate the Concept class
                Concept concept = new Concept();
                Set<String> context = new HashSet<String>();
                String desc = null;

                // sets the ontology into the concept class
                man.configOwlOntology(concept, onto);
                // sets the owlclass into the concept class
                man.configOwlClass(concept, owlClass);
                // sets the owlclassID into the concept class
                man.configClassId(concept, owlClass.toString());
                // sets the owlclass name into the concept class
                man.configClassName(concept, owlClass.getIRI().getFragment());
                // desc receive the annotation of a concept
                desc = extractAnnotationUpperWE(onto, owlClass, context);
                // sets the description into the concept class
                man.configDescription(concept, desc);

                context.add(owlClass.getIRI().getFragment());
                man.configContext(concept, context);

                // adds the concept into a list
                listCon.add(concept);
            }
        }
        finalLogUpper();
        return listCon;
    }


    /**
     * Extracts the annotation of the top onto. concept
     */
    private String extractAnnotationUpperWE(OWLOntology onto, OWLClass cls, Set<String> context) {
        String desc = null;
        for (OWLAnnotation anno : cls.getAnnotations(onto)) {
            //get the annotation at comment label
            if (anno.getProperty().getIRI().getFragment().equals("comment") && anno != null && context != null) {
                String aux = anno.getValue().toString();
                if (aux != null) {
                    desc = rmSuffix(aux);
                    context.add(desc);
                }
            }
        }
        return desc;
    }

    private String extractAnnotationUpper(OWLOntology onto, OWLClass cls) {
        String aux = null;
        for (OWLAnnotation anno : cls.getAnnotations(onto)) {
            aux = anno.getValue().toString();
        }
        return aux;
    }


    /**
     * Extracts the annotation of the domain ont. concept
     */
    private String extractAnnotation(OWLOntology onto, OWLClass cls, Set<String> cntxt) {
        String desc = null;
        for (OWLAnnotation anno : cls.getAnnotations(onto)) {
            String aux2 = null;
            //get the annotation at comment label
            if (anno.getProperty().getIRI().getFragment().equals("comment") && anno != null) {
                String aux = anno.getValue().toString();
                aux = rmSuffix(aux);
                aux2 = aux;

                aux = removeSpecialChar(aux);
                cntxt.add(aux);
                //get the annotation at definition label
            } else if (anno.getProperty().getIRI().getFragment().equals("definition") && anno != null) {
                String aux = anno.getValue().toString();
                aux = rmSuffix(aux);
                aux2 = aux;

                aux = removeSpecialChar(aux);
                cntxt.add(aux);
                //get the annotation at example label
            } else if (anno.getProperty().getIRI().getFragment().equals("example") && anno != null) {
                String aux = anno.getValue().toString();
                aux = rmSuffix(aux);
                aux2 = aux;

                aux = removeSpecialChar(aux);
                cntxt.add(aux);
            }
            //condition that avoid the apparition of null and repeated elements into the concept description
            if (desc == null && aux2 != null) {
                desc = aux2;
            } else if ((desc != null && aux2 != null) && !desc.contains(aux2)) {
                desc = desc + aux2;
            }

        }
        return desc;
    }


    /**
     * Extracts the super classes of a owlClass
     */
    protected void extractSuperClass(OWLOntology onto, OWLClass cls, Set<String> cntxt, List<OWLClassExpression> list) {
        for (OWLClassExpression sup : cls.getSuperClasses(onto)) {
            if (!sup.isAnonymous()) {
                if(cntxt!=null) cntxt.add(sup.asOWLClass().getIRI().getFragment());
                list.add(sup);
                extractSuperRecurClass(onto, sup, cntxt, list);

            }
        }
    }


    /**
     * Recursive call of extractSuperClass
     */
    protected void extractSuperRecurClass(OWLOntology onto, OWLClassExpression su, Set<String> cntxt, List<OWLClassExpression> list) {
        if (su != null) {
            for (OWLClassExpression sup : su.asOWLClass().getSuperClasses(onto)) {
                if (!sup.isAnonymous()) {
                    list.add(sup);
                    if(cntxt!=null) cntxt.add(sup.asOWLClass().getIRI().getFragment());
                    extractSuperRecurClass(onto, sup, cntxt, list);
                }
            }
        }
    }


    /**
     * Extracts the sub classes of a owlClass
     */
    protected void extractSubClass(OWLOntology onto, OWLClass cls, Set<String> cntxt, List<OWLClassExpression> list) {
        for (OWLClassExpression sub : cls.getSubClasses(onto)) {
            if (!sub.isAnonymous() && cntxt != null) {
                cntxt.add(sub.asOWLClass().getIRI().getFragment().toString());
                list.add(sub);

                extractSubRecurClass(onto, sub, cntxt, list);

            }
        }
    }


    /**
     * Recursive call of extractSubClass
     */
    protected void extractSubRecurClass(OWLOntology onto, OWLClassExpression su, Set<String> cntxt, List<OWLClassExpression> list) {
        if (su != null) {
            for (OWLClassExpression sub : su.asOWLClass().getSubClasses(onto)) {
                if (!sub.isAnonymous()) {
                    list.add(sub);
                    cntxt.add(sub.asOWLClass().getIRI().getFragment().toString());

                    extractSubRecurClass(onto, sub, cntxt, list);
                }
            }
        }
    }


//Auxiliary methods 

    /**
     * Removes the suffix of the annotation
     */
    private String rmSuffix(String aux) {
        if (aux.endsWith("^^xsd:string")) {
            aux = aux.replace("^^xsd:string", "");
        }
        if (aux.endsWith("@en")) {
            aux = aux.replace("@en", "");
        }

        return aux;
    }


    /**
     * Verifies if the list contains owl:Thing
     */
    private boolean verifyThing(List<OWLClassExpression> list) {
        if (list.get(0).asOWLClass().isTopEntity()) {
            return true;
        }
        return false;
    }


    /**
     * Removes some chars of a string
     */
    private String removeSpecialChar(String word) {

        if (!isSite(word)) {
            String aux = word;
            char x = '"';
            String z = String.valueOf(x);

            if (aux.contains("  ")) {
                aux = aux.replaceAll("  ", " ");
            }

            if (aux.contains(z)) {
                aux = aux.replace(z, "");
            }

            if (aux.contains(".")) {
                aux = aux.replace(".", " ");
            }

            if (aux.contains(",")) {
                aux = aux.replace(",", "");
            }

            if (aux.contains("?")) {
                aux = aux.replace("?", " ");
            }

            if (aux.contains(":")) {
                aux = aux.replace(":", " ");
            }

            if (aux.contains("!")) {
                aux = aux.replace("!", " ");
            }

            if (aux.contains("  ")) {
                aux = aux.replaceAll("  ", " ");
            }
            return aux;
        }
        return word;
    }


    private boolean isSite(String word) {
        if (word.contains("http:")) {
            return true;
        }
        return false;
    }
}
