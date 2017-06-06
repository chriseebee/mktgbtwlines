package uk.me.chriseebee.mktgbtwlines2.db;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.AvailabilityException;
import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;
import uk.me.chriseebee.mktgbtwlines2.nlp.InterestingEvent;

public class StorageListenerThread extends Thread {
		
	Logger logger = LoggerFactory.getLogger(StorageListenerThread.class);
	
    private final ConcurrentLinkedQueue<InterestingEvent> queue;
    private volatile boolean running = true;
    OrientClient storageClient = null;
    
    public StorageListenerThread() throws StorageException {
    	try {
			storageClient = new OrientClient();
		} catch (ConfigurationException | AvailabilityException e) {
			logger.error("Error initialising Agens: "+e.getMessage());
			throw new StorageException (e);
		}
        this.queue = ThreadCommsManager.getInstance().getStorageMessageQueue();
    }
	  
    public void run() {
        while ( running ) {
        	InterestingEvent ev = queue.poll();
            if (ev!=null) {
            	try {
					storageClient.sendEventToDataStore(ev);
				} catch (StorageException e) {
					logger.error("Failed to Store the Event!");
				}
            }
            try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
    public void stopRunning() {
    	this.running = false;
    }    
}
