package objects;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/*
 *Ontology class contains all the information about a OWLOntology loaded
 */
public class Ontology {

//Attributes
	
	//Ontology file name
	private String fileName;
	//Ontology ID
	private OWLOntologyID ontologyID;
	//OWLOntology loads the ontology file
	private OWLOntology ontology;
	//Used to manipulate de ontology
	private OWLOntologyManager manager;


//Constructor
		
	/*
	* creates a new Ontology class and extract the necessary information 
	* about the ontology with shouldLoad method. 
	*/
	public Ontology(String _file) {
		try {
			initLog();
			shouldLoad(_file);
			finalLog();
		} catch(OWLOntologyCreationException e) {
			System.out.println("Failed to load ontology: " + _file);
			System.out.println("erro: " + e);
		} catch(FileNotFoundException e) {
			System.out.println("File not found: " + _file);
			System.out.println("erro: " + e);
		} catch(IOException e) {
			System.out.println("I/O operation failed: " + _file);
			System.out.println("erro: " + e);
		}

	}


//Log Methods
	
	private void initLog() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Loading ontology..." );
	}


	private void finalLog() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Ontology loaded!" );
	}


//Getters

    public String getFileName() {
    return fileName;
}

    public OWLOntologyID getOntologyID() {
        return ontologyID;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    protected OWLOntologyManager getOntologyManager() {
        return manager;
    }

//Setters

    private void setFileName(String _file) {
		int i = _file.lastIndexOf("/");
		String fname;
		fname = _file.substring(i);
		fileName = fname;
	}

	protected void setOntologyID(OWLOntologyID ontoID) {
		ontologyID = ontoID;
	}

	protected void setOntology(OWLOntology onto) {
		ontology = onto;
	}

	protected void setOntologyManager(OWLOntologyManager _manager) {
		manager = _manager;
	}


//Methods
	
	/*
	 * Loads the ontology into OWLOntology and extract the Ontology ID, IRI and manager.
	 */	
	protected void shouldLoad(String _file) throws FileNotFoundException, IOException, OWLOntologyCreationException {
		
		File file = new File(_file);
		
		setFileName(_file);
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		OWLOntology local = manager.loadOntologyFromOntologyDocument(file);
		IRI documentoIRI = manager.getOntologyDocumentIRI(local);
		
		setOntologyManager(manager);
		setOntology(local);
		
		//System.out.println("Loaded Ontology: " + local);
		//System.out.println("From: " + documentoIRI + "\n");
		
		OWLOntologyID ID = local.getOntologyID();
		setOntologyID(ID);
		
		//System.out.println(ID);
		System.out.println("\nOntology loaded: " + ID.getOntologyIRI());
		System.out.println("From: " + documentoIRI + "\n");
	}
	
}
