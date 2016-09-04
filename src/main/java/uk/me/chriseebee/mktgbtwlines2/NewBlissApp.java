package uk.me.chriseebee.mktgbtwlines2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines.speech2text.Speech2TextClientThread;
import uk.me.chriseebee.mktgbtwlines2.audio.AudioRecorder;
import uk.me.chriseebee.mktgbtwlines2.audio.NoiseTrigger;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.db.StorageListenerThread;
import uk.me.chriseebee.mktgbtwlines2.nlp.NLPListenerThread;


public class NewBlissApp {
	
	Logger logger = LoggerFactory.getLogger(NewBlissApp.class);

	private NoiseTrigger nt = null;
	private AudioRecorder ar = null;
	private Speech2TextClientThread stct = null;
	private NLPListenerThread nlplt =  null;
	private StorageListenerThread slt = null;
	
	private Thread t1 = null;
	private Thread t2 = null;
	private Thread t3 = null;
	private Thread t4 = null;
	private Thread t5 = null;
			
	public NewBlissApp(String filePath) throws Exception {
		ConfigLoader.getConfigLoader(filePath);
	}

	public void init() throws Exception {
		// We need a number of threads
		// 1. A microphone listener that triggers the main 
		//    sound recording when noise happens. THis will always
		//    be running
		nt = new NoiseTrigger();
	    t1 = new Thread (nt);
	    
		//
		// 2. A sound recorder thread that takes the sound and
		//    chunks it up into byte[] chunks to fire at the API
	    //
	    ar = new AudioRecorder();
	    t2 = new Thread (ar);
	    
		//
	    // 3. A speech recog API that picks up the chunks and processes
	    //
	    stct = new Speech2TextClientThread();
	    t3 = new Thread (stct);
	    
		// 4. An NLP thread that consumes the responses from the speech 
		//    to text API, processes them and raises events if required
		//
	    nlplt = new NLPListenerThread();
	    t4 = new Thread (nlplt);
	    
		// 5. A thread that pushes the events to Influx DB from a Queue
	    //
	    slt = new StorageListenerThread();
	    t5 = new Thread (slt);
	
		// Start the threads in reverse dependency order
	    logger.info("STARTING STORAGE THREAD");
	    t5.start();
	    logger.info("STARTING NLP THREAD");
	    t4.start();
	    logger.info("STARTING SPEECH TO TEXT THREAD");
	    t3.start();    
	    logger.info("STARTING RECORDING THREAD");
	    t2.start();
	    logger.info("STARTING MIC LISTENER THREAD");
	    t1.start();
	}
	
	public void shutdown() {
		
		logger.warn("SHUTTING DOWN THREADS");
		nt.stopRunning();
		ar.stopRunning();
		stct.stopRunning();
		nlplt.stopRunning();
		slt.stopRunning();
	}
	
	public static void main( String[] args )
    {
		System.out.println("---------------------------------------");
		System.out.println("------ APPLICATION STARTING UP --------");
		System.out.println("---------------------------------------");
    	NewBlissApp app = null;
    	try {
        
    		
    		String fileName = "";
    		if (args.length==0) {
    			fileName = "config.yml";
    		} else if (args[0]!=null) {
    			fileName = args[0];
    		}
	    	app = new NewBlissApp(fileName);
	    	
	    	app.init();

    	} catch ( Exception e) {
	    	System.out.println("Error in main launch");
	    	e.printStackTrace();
	    } 
    	
    	System.out.println("---------------------------------------");
    	System.out.println("----- APPLICATION SHUTTING DOWN -------");
    	System.out.println("---------------------------------------");
	    
    }
}
