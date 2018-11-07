package objects;

import java.util.HashMap;
import java.util.Map.Entry;

public class OutObjectWE implements Comparable<OutObjectWE> {

//Attributes

	private Concept cnpUp;
	private HashMap<String, Object> map;
	private Double[] vec;
	private double mediaT;


//Constructor
	public OutObjectWE(int size) {
		cnpUp = null;
		map = new HashMap<String, Object>();
		vec = new Double[size];
		mediaT = 0;
	}


//Getters

	public Concept getTopConcept() {
		return this.cnpUp;
	}
	
	public HashMap<String, Object> getMap() {
		return this.map;
	}
	
	public Double[] getVector() {
		return this.vec;
	}
	
	public double getTotalAverage() {
		return this.mediaT;
	}


//Setters

    public void setTopConcept(Concept cnp) {
        this.cnpUp = cnp;
    }

	public void setMap(HashMap<String, Object> map) {
		this.map = map;
	}

	public void setVector(Double[] vec) {
		this.vec = vec;
	}

	public void setTotalAverage(double media) {
		this.mediaT = media;
	}


//Methods

	@SuppressWarnings("unchecked")
	public void info() {
		System.out.println("NAME: " + cnpUp.getClassName());
		System.out.println("SIMILARIDADE:");
		int aux = 0;
		for(Entry<String, Object> entry: map.entrySet()) {
			String key = entry.getKey();
			HashMap<String, Double> value = (HashMap<String, Double>) entry.getValue();
			System.out.print("\t" + key + "=" + vec[aux] + " | ");
			
			for(Entry<String, Double> entr: value.entrySet()) {
				String ky = entr.getKey();
				double val = entr.getValue();
				System.out.print(ky + "=" + val + "; ");
			}
			aux++;
			System.out.println("\n");
		}
		System.out.println("TOTAL AVERAGE: " + mediaT);
	}


	@Override
	public int compareTo(OutObjectWE o) {
		if(this.mediaT > o.getTotalAverage()) {
			return -1;
		} else if(this.mediaT < o.getTotalAverage()) {
			return 1;
		}
		return 0;
	}
	

}
