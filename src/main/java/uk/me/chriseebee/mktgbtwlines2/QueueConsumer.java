package uk.me.chriseebee.mktgbtwlines2;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueConsumer extends Thread {
	
    private final ConcurrentLinkedQueue<String> queue;
    private String buffer  = "";
    private int counter = 0;
    private volatile boolean running = true;
   
    
    public QueueConsumer() {
    	influxClient = new InfluxClient();
        this.queue = CollectionManager.getInstance().queue;
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
	   
    private void doWork(String s) {
    	
    	if (counter == 0) {
    		buffer = s;
    		counter++;
    	} else if (counter < 10) {
    		buffer = buffer + "\n" + s;
    		counter++;
    	}
    	
    	if (counter==10) {
    		try {
				sendPOST();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		counter = 0;
    		buffer = "";
    	}
    }
	    

	
}