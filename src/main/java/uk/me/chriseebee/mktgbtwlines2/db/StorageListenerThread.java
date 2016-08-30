package uk.me.chriseebee.mktgbtwlines2.db;

import java.util.concurrent.ConcurrentLinkedQueue;

import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;
import uk.me.chriseebee.mktgbtwlines2.db.influx.InfluxClient;
import uk.me.chriseebee.mktgbtwlines2.nlp.InterestingEvent;

public class StorageListenerThread extends Thread {
		
    private final ConcurrentLinkedQueue<InterestingEvent> queue;
    private volatile boolean running = true;
    InfluxClient influxClient = null;
    
    public StorageListenerThread() {
    	influxClient = new InfluxClient();
        this.queue = ThreadCommsManager.getInstance().getInfluxMessageQueue();
    }
	  
    public void run() {
        while ( running ) {
        	InterestingEvent ev = queue.poll();
            if (ev!=null) {
            	influxClient.sendEventToInflux(ev);
            }
        }
    }
    
    public void stopRunning() {
    	this.running = false;
    }    
}
