package uk.me.chriseebee.mktgbtwlines2.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.audio.TimedAudioBuffer;
import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;

public class AudioClipStoreThread extends Thread {

	Logger logger = LoggerFactory.getLogger(AudioClipStoreThread.class);

	private volatile boolean running = true;
	
	public AudioClipStoreThread() {
	}

	public void run() {
	    while ( running ) {
	        TimedAudioBuffer tab = ThreadCommsManager.getInstance().getAudioBufferQueue().poll();
	        if (tab!=null) {
	        	AudioClipStore.getInstance().put(tab);
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
