package resources;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;

import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

/**
 * This class makes the evaluation between the alignment generated and the reference alignment
 */
public class Evaluator {
	
//Attributes
	
	//Path to the reference alignment
	String ref;
	//Path to the generated alignment
	String alin;
	//The evaluator
	PRecEvaluator evaluator;


//Constructor
	
	public Evaluator(String ref, String alin) {
		setAlignment(alin);
		setReference(ref);
	}


//Log methods
	
	private void log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Evaluating the matching..." );
	}


//Getters

    protected PRecEvaluator getEvaluator() {
        return this.evaluator;
    }

	String getReference() {
		return this.ref;
	}

    String getAlignment() {
        return this.alin;
    }


//Setters

    void setReference(String _ref) {
        this.ref = _ref;
    }

	void setAlignment(String _alin) {
		this.alin = _alin;
	}


//Methods
	
	/**
	 * This method makes the evaluation
	 */
	public void evaluate() {
		Alignment ref;
		Alignment alin;
		
		//The parser shows how to analyze the XML file
		AlignmentParser ap1 = new AlignmentParser(0);
		AlignmentParser ap2 = new AlignmentParser(0);
		log();
		try {
			ref = ap1.parse(new File(getReference()).toURI() );
			alin = ap2.parse(new File(getAlignment()).toURI());
			
			Properties p = new Properties();
			//Instantiation of the evaluator
			//Always put the reference alignment as the first argument
			PRecEvaluator eva = new PRecEvaluator(ref, alin);
			eva.eval(p);
			System.out.println("REF - ALI:\n" + "F-Measure: " + eva.getFmeasure() + "\nPrecision:  " + eva.getPrecision() + "\nRecall: " + eva.getRecall() + "\nOverall: " + eva.getOverall());
		} catch (AlignmentException e) {
			System.out.println("error: Reference-Alignment, or Alignment: format");
			e.printStackTrace();
		}
	}

}
