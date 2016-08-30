package uk.me.chriseebee.mktgbtwlines2;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import uk.me.chriseebee.mktgbtwlines.speech2text.ibm.App;
import uk.me.chriseebee.mktgbtwlines2.audio.NoiseTrigger;

public class NewBlissApp {

	public NewBlissApp() {

		
		// We need a number of threads
		// 1. A microphone listener that triggers the main 
		//    sound recording when noise happens. THis will always
		//    be running
		NoiseTrigger nt = new NoiseTrigger();
	    Thread t1 = new Thread (nt);
	    
		//
		// 2. A sound recorder thread that takes the sound and
		//    chunks it up into 10s byte[] chunks to fire at the API
	    AudioRecorder ar = new AudioRecorder();
	    Thread t2 = new Thread (ar);
	    
		//
		// 3. An NLP thread that consumes the responses from the speech 
		//    to text API, processes them and raises events if required
		//
		// 4. A thread that pushes the events to Influx DB from a Queue
		// 
	
		// Start the threads in reverse dependency order
        
	    t2.start();
	    t1.start();
	}
	
    public static void main( String[] args )
    {
    	NewBlissApp app = new NewBlissApp();

    }
}
