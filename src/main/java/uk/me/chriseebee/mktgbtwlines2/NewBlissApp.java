package uk.me.chriseebee.mktgbtwlines2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines.speech2text.Speech2TextClientThread;
import uk.me.chriseebee.mktgbtwlines2.audio.AudioRecorder;
import uk.me.chriseebee.mktgbtwlines2.audio.NoiseTrigger;
import uk.me.chriseebee.mktgbtwlines2.nlp.NLPListenerThread;

public class NewBlissApp {
	
	Logger logger = LoggerFactory.getLogger(NewBlissApp.class);

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
	    // 3. A speech recog API that picks up the chunks and processes
	    //
	    Speech2TextClientThread stct = new Speech2TextClientThread();
	    Thread t3 = new Thread (stct);
	    
	    
		// 4. An NLP thread that consumes the responses from the speech 
		//    to text API, processes them and raises events if required
		
	    NLPListenerThread nlplt = new NLPListenerThread();
	    Thread t4 = new Thread (nlplt);
	    
		// 5. A thread that pushes the events to Influx DB from a Queue
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
