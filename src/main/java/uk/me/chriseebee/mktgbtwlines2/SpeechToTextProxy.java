package uk.me.chriseebee.mktgbtwlines2;

import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.darkprograms.speech.microphone.MicrophoneAnalyzer;

//import uk.me.chriseebee.mktgbtwlines2.audio.NoiseTrigger;
import uk.me.chriseebee.mktgbtwlines2.audio.AudioStreamer;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;

public class SpeechToTextProxy  {

	Logger logger = LoggerFactory.getLogger(SpeechToTextProxy.class);

    //private NoiseTrigger nt = null;
    private AudioStreamer ar = null;

    //private Thread t1 = null;
    private Thread t2 = null;
    
    public SpeechToTextProxy (String filepath) throws Exception {
		ConfigLoader.getConfig(filepath);
	}

    public void init(String mode, File audioFile) throws Exception {
		// We need a number of threads
		// 1. A microphone listener that triggers the main 
		//    sound recording when noise happens. THis will always
		//    be running
		//nt = new NoiseTrigger(new MicrophoneAnalyzer());
	    //t1 = new Thread (nt);
	    
		//
		// 2. A sound recorder thread that takes the sound and
		//    chunks it up into byte[] chunks to fire at the API
	    //
	    ar = new AudioStreamer(ConfigLoader.getConfig().getS2tParams().get("hostname"),ConfigLoader.getConfig().getS2tParams().get("port"));
		ar.setSourceOption(mode,audioFile);
		t2 = new Thread (ar);

        logger.info("STARTING RECORDING THREAD");
	    t2.start();
	    //logger.info("STARTING MIC LISTENER THREAD");
	    //t1.start();
    }

    public void shutdown() {
		
		logger.warn("SHUTTING DOWN THREADS");
		//nt.stopRunning();
		ar.stopRunning();
    }
    
    public static void main( String[] args )
    {
		System.out.println("---------------------------------------");
		System.out.println("------ APPLICATION STARTING UP --------");
		System.out.println("---------------------------------------");

		String configFileName = "";
		File f = null;
		if (args.length==2) {
			// then we are in mic mode as long as arg2 is LINE
			if (!args[1].equals("LINE")) {
				System.out.println("Can't have 2 args unless in LINE mode");
				System.exit(1);
			}
		} else {
			if (args.length!=3) {
				// then its a mess
				System.out.println("Need 3 args - config file, mode, filename");
				System.exit(1);
			} else {
				// we are in file mode
				// in which case
				if (!args[1].equals("FILE")) {
					System.out.println("Can't have 2 args unless in LINE mode");
					System.exit(1);
				}
                f = new File(args[2]);
			}
		}

		configFileName = args[0];

    	SpeechToTextProxy  app = null;
    	try {
        
	    	app = new SpeechToTextProxy (configFileName);
	    	
	    	app.init(args[1],f);

    	} catch ( Exception e) {
	    	System.out.println("Error in main launch");
	    	e.printStackTrace();
	    } 
    	
    	
//    	Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//            public void run() {
//                //code to close connection
//            }
//        }, "Shutdown-thread"));
    	
    	System.out.println("---------------------------------------");
    	System.out.println("------- APPLICATION STARTED -----------");
    	System.out.println("---------------------------------------");
	    
    }
}