package uk.me.chriseebee.mktgbtwlines.speech2text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines.speech2text.google.GoogleClientApp;
import uk.me.chriseebee.mktgbtwlines2.audio.TimedAudioBuffer;
import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.mappers.AppConfig;


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
			ConfigLoader cl = ConfigLoader.getConfigLoader();
			ac = cl.getConfig();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("BIG ERROR LOADING CONFIG",e);
		}
		
    	mode = ac.getAudioOptions().get("whichService");
    	
    	gca = new GoogleClientApp();
    }
    
    public void run() {
        while ( running ) {
            TimedAudioBuffer tab = ThreadCommsManager.getInstance().getAudioBufferQueue().poll();
            if (tab!=null) {
            	gca.processBuffer(tab);
            }
        }
    }
    
    public void stopRunning() {
    	this.running = false;
    }
}
