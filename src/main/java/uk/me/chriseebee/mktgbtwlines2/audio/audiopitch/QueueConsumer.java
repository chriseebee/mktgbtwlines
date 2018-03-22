package uk.me.chriseebee.audiopitchjava;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueConsumer extends Thread {
	
    private final ConcurrentLinkedQueue<String> queue;
    
    private volatile boolean running = true;
    
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String POST_URL = "http://SERVER:8086/write?db=laughter";
    private String buffer  = "";
    private int counter = 0;
    String influxHostname = "";
    URL obj;
    
    public QueueConsumer(String influxHostname) {
    	this.influxHostname = influxHostname;
    	try {
			obj = new URL(POST_URL.replace("SERVER", influxHostname));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	    
    private void sendPOST() throws IOException {
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
 
        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(buffer.getBytes());
        os.flush();
        os.close();
        // For POST only - END
 
        int responseCode = con.getResponseCode();
 
        if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
        	System.out.println("POST request to InfluxDB has not worked: "+responseCode);
        } 
        
    }
	
}
