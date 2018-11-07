package resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;

/*
 * This class contains and initiate the resources used in the process 
 */
public class BaseResource {

//Attributes
	
	//Stop words list
	private List<String> stpWords = new ArrayList<String>();
	//Lemmatizer resource
	private StanfordLemmatizer slem;
	//Word2Vector resource
	private Word2Vector w2v;

//Constructors
	
	/*
	 * Default
	 */
	public BaseResource() {
			init_log();
			rd_StpWords();
			this.slem = new StanfordLemmatizer();
			this.w2v = null;
	}
	
	/*
	 * This constructor receive an integer and select based on 
	 * the technique select which resources should be initialized  
	 */
	public BaseResource(int x, String model) {

		if(x == 1) {
			init_log();
			rd_StpWords();
			this.slem = new StanfordLemmatizer();
			this.w2v = null;

		} else if(x == 2) {
			init_log();
			rd_StpWords();
			this.slem = new StanfordLemmatizer();
			this.w2v = new Word2Vector(model);

		} else {
			throw new InvalidParameterException("Parameters lead to an invalid option for resourcing.");
		}
	}
	
//Log methods
	
	private void init_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Initializing resources!" );
	}
	
	private void stpWords_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Reading Stop Words..." );
	}

//Reader method
	/*
	 * This method read the stopwords2.text file, 
	 * and put the lines into a list
	 */

	private void rd_StpWords() {
		stpWords_log();
		try {
			BufferedReader br = new BufferedReader(new FileReader("resources/stopwords2.txt"));
			String line;
			
			while((line = br.readLine()) != null) {
				this.stpWords.add(line.toLowerCase());
			}
			br.close();
		} catch(FileNotFoundException e) {
	    	System.out.println("StopWords file was not found!");
	    	System.out.println("error: " + e);
	    } catch (IOException e) {
	    	System.out.println("I/O operation failed - StopWords file!");
	    	System.out.println("error: " + e);
	    }
	}

//Getters
	
	public List<String> get_StpWords() {
		return this.stpWords;
	}

	public StanfordLemmatizer get_lemmatizer() { return this.slem; }
	
	public Word2Vector get_word2vec() {
		return this.w2v;
	}
	

}
