package uk.me.chriseebee.mktgbtwlines2.db.influx;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.nlp.InterestingEvent;

public class InfluxClient {

	Logger logger = LoggerFactory.getLogger(InfluxClient.class);
	
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String POST_URL = "http://SERVER:8086/write?db=newbliss";

    String influxHostname = "";
    URL obj;
    
	public InfluxClient() {
		//TODO: Replace ports with configuration calls
		ConfigLoader cl = null;
		try {
			cl = ConfigLoader.getConfigLoader();
		} catch (Exception e1) {
			logger.error("Failed to get Configuration",e1);
		}
		String influxHostname = cl.getConfig().getInfluxParams().get("hostname");
		
    	try {
			obj = new URL(POST_URL.replace("SERVER", influxHostname));
		} catch (MalformedURLException e) {
			logger.error("Failed to get Server details for Influx",e);
		}
	}
	
	
    public void sendEventToInflux(InterestingEvent ev) throws IOException {
        
    	logger.info("Sending interesting event to Influx: "+ev.toString());
		java.util.Date date= new java.util.Date();
		String message = ev.toString();
    	
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
 
        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(message.getBytes());
        os.flush();
        os.close();
        // For POST only - END
 
        int responseCode = con.getResponseCode();
 
        if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
        	logger.error("POST request to InfluxDB has not worked: "+responseCode);
        } 
        
    }
    

}
