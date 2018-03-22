package uk.me.chriseebee.mktgbtwlines2.speech2text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.speech2text.google.GoogleClientApp;
import uk.me.chriseebee.mktgbtwlines2.audio.TimedAudioBuffer;
import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.mappers.AppConfig;
import uk.me.chriseebee.mktgbtwlines2.storage.AudioClipStore;


public class Speech2TextClientThread extends Thread {

	Logger logger = LoggerFactory.getLogger(Speech2TextClientThread.class);
	
    private String mode = "GOOGLE";
    private String buffer  = "";
    private int counter = 0;
    private volatile boolean running = true;
    
    private GoogleClientApp gca = null;
    
    public Speech2TextClientThread() {
    	
		AppConfig ac = null; 
		try {
			ac = ConfigLoader.getConfig();
			
	    	mode = ac.getAudioOptions().get("whichService");
	    	
	    	gca = new GoogleClientApp();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("BIG ERROR LOADING CONFIG",e);
		}
    }
    
    public void run() {
        while ( running ) {
            TimedAudioBuffer tab = ThreadCommsManager.getInstance().getSentenceInSpeechDetectionQueue().poll();
            if (tab!=null) {
            	try {
					AudioClipStore.getInstance(AudioClipStore.STORE_TYPE_MEMORY).get(tab);
				} catch (Exception e) {
					logger.error("Error: "+e.getLocalizedMessage());
				}
            	gca.processBuffer(tab);
            }
        }
        try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void stopRunning() {
    	this.running = false;
    }
}
