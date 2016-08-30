package uk.me.chriseebee.mktgbtwlines2.db.influx;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.nlp.InterestingEvent;

public class InfluxClient {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String POST_URL = "http://SERVER:8086/write?db=newbliss";

    String influxHostname = "";
    URL obj;
    
	public InfluxClient() {
		
		ConfigLoader cl = null;
		try {
			cl = ConfigLoader.getConfigLoader();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String influxHostname = cl.getConfig().getInfluxParams().get("hostname");
		
    	try {
			obj = new URL(POST_URL.replace("SERVER", influxHostname));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
    private void sendPOST(String buffer) throws IOException {
        
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
    
    public void sendEventToInflux(InterestingEvent ev) {
		java.util.Date date= new java.util.Date();
		String ts = date.getTime()+"000000";
		String message = "laughcalc value="+tempVal+",range="+q.getRange()+",average="+avg+" "+ts;
		queue.offer(message);
    }
}
