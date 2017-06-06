package uk.me.chriseebee.mktgbtwlines2.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.audio.TimedAudioBuffer;
import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;

public class AudioClipStoreThread extends Thread {

	Logger logger = LoggerFactory.getLogger(AudioClipStoreThread.class);

	private volatile boolean running = true;
	
	public AudioClipStoreThread() {
		logger.info("Adding shutdown hook for AudioClipStore");
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { try {
				AudioClipStore.getInstance(AudioClipStore.STORE_TYPE_DATABASE).closeDatabase();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} }
		});
		
	}

	public void run() {
	    while ( running ) {
	        TimedAudioBuffer tab = ThreadCommsManager.getInstance().getAudioBufferQueue().poll();
	        if (tab!=null) {
	        	try {
					AudioClipStore.getInstance(AudioClipStore.STORE_TYPE_DATABASE).put(tab);
				} catch (Exception e) {
					logger.error("Error: "+e.getLocalizedMessage());
				}
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
