package matchingProcess;

/**
 * This class maps the alignment between domain Ont. classes and top Ont. classes
 */
public class Mapping {
	
//Attributes
	
	//source entity
	private String source;
	//target entity
	private String target;
	//relation between source and target
	private String relation;
	//trust measure
	private String measure;


//Getters
	
	String getSource(){
		return source;
	}

	String getTarget() {
		return target;
	}

    String getRelation() {
        return relation;
    }

    String getMeasure() {
        return measure;
    }


//Setters

    void setSource(String _source) {
        source = _source;
    }

    void setTarget(String _target) {
        target = _target;
    }

    void setRelation(String _relation) { relation = _relation; }

	void setMeasure(String _measure) { measure = _measure; }
}
