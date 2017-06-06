package uk.me.chriseebee.mktgbtwlines2.audio;


import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines.speech2text.Speech2TextClientThread;
import uk.me.chriseebee.mktgbtwlines2.audio.NoiseTrigger;
import uk.me.chriseebee.mktgbtwlines2.db.StorageListenerThread;
import uk.me.chriseebee.mktgbtwlines2.nlp.NLPListenerThread;
import uk.me.chriseebee.mktgbtwlines2.storage.AudioClipStoreThread;

public class NoiseTriggerTest {

	Logger logger = LoggerFactory.getLogger(NoiseTriggerTest.class);
	

	private NoiseTrigger nt = null;
	private AudioRecorder ar = null;
	private Speech2TextClientThread stct = null;
	private NLPListenerThread nlplt =  null;
	private StorageListenerThread slt = null;
	private AudioClipStoreThread acs = null;
	
	private Thread t1 = null;
	private Thread t2 = null;
	private Thread t3 = null;
	private Thread t4 = null;
	
	@Test
	public void test() throws FileNotFoundException {
		logger.info("In Test");
		
		//AudioUtils.printMixers();
		//AudioUtils.printMixerSupport(AudioSystem.getMixer(AudioSystem.getMixerInfo()[0]));
		
		
		MicrophoneAnalyzerFake maf = new MicrophoneAnalyzerFake();
		maf.setupFile(new File("/Users/cbell/Desktop/MorganSpurlock_2011_16.wav"));
        nt = new NoiseTrigger(maf);
        nt.setThreshold(40);
	    t1 = new Thread (nt);
	    
	    acs = new AudioClipStoreThread();
	    t3 = new Thread (acs);

	    //stct = new Speech2TextClientThread();
	    //t4 = new Thread (stct);
		
	    //t4.start();    
	    logger.info("STARTING CLIP THREAD");
	    t3.start();

	    logger.info("STARTING MIC LISTENER THREAD");
	    t1.start();
	
	
	    try {
			Thread.sleep(120000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    nt.stopRunning();
	}

}
