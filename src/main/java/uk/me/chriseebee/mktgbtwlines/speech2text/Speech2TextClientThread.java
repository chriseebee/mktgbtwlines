package uk.me.chriseebee.mktgbtwlines.speech2text;

import static org.junit.Assert.fail;

import java.util.concurrent.ConcurrentLinkedQueue;

import uk.me.chriseebee.mktgbtwlines2.audio.TimedAudioBuffer;
import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.mappers.AppConfig;


public class Speech2TextClientThread extends Thread {

	
    private String mode = null;
    private String buffer  = "";
    private int counter = 0;
    private volatile boolean running = true;
    
    public Speech2TextClientThread() {
    	
    	
		AppConfig ac = null; 
		try {
			ConfigLoader cl = ConfigLoader.getConfigLoader();
			ac = cl.getConfig();
		} catch (Exception e) {
			e.printStackTrace();
			fail("BIG ERROR LOADING CONFIG");
		}
		
    	mode = ac.getAudioOptions().get("whichService");
    }
    
    public void run() {
        while ( running ) {
            String s = queue.poll();
            if (s!=null) {
            	doWork(s);
            	//System.out.println(s);
            }
        }
    }
    
    public void stopRunning() {
    	this.running = false;
    }
}
