package uk.me.chriseebee.mktgbtwlines2.db.influx;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.nlp.InterestingEvent;

public class InfluxClient {

	Logger logger = LoggerFactory.getLogger(InfluxClient.class);
	
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String POST_URL = "http://SERVER:8086/write?db=newbliss";

    String influxHostname = "";
    String username = null;
    String password = null;
    URL obj; 
    
	public InfluxClient() {
		//TODO: Replace ports with configuration calls
		ConfigLoader cl = null;
		try {
			cl = ConfigLoader.getConfigLoader();
		} catch (Exception e1) {
			logger.error("Failed to get Configuration",e1);
		}
		influxHostname = cl.getConfig().getInfluxParams().get("hostname");
		username = cl.getConfig().getInfluxParams().get("username");
		password = cl.getConfig().getInfluxParams().get("password");
		
    	try {
			obj = new URL(POST_URL.replace("SERVER", influxHostname));
		} catch (MalformedURLException e) {
			logger.error("Failed to get Server details for Influx",e);
		}
	}
	
	
    public void sendEventToInflux(InterestingEvent ev) throws IOException {
        
    	logger.info("Sending interesting event to Influx: "+ev.toString());
		String message = ev.toString();
    	
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        
        String authStr = username+":"+password;

     // encode data on your side using BASE64
        byte[] bytesEncoded = Base64.encodeBase64(authStr .getBytes());
        String authEncoded = new String(bytesEncoded);
        
        con.setRequestProperty("Authorization", "Basic "+authEncoded);
 
        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(message.getBytes());
        os.flush();
        os.close();
        // For POST only - END
 
        int responseCode = con.getResponseCode();
        
        logger.info("message = "+con.getResponseMessage());
        //logger.info("message: "+ con.getr);
 
        if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
        	logger.error("POST request to InfluxDB has not worked: "+responseCode);
        } 
        
    }
    
    
    public void sendEventToInflux(String text) throws IOException {
        
    	logger.info("Sending interesting event to Influx: "+text);
    	
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        
        String authStr = username+":"+password;

     // encode data on your side using BASE64
        byte[] bytesEncoded = Base64.encodeBase64(authStr .getBytes());
        String authEncoded = new String(bytesEncoded);

        con.setRequestProperty("Authorization", "Basic "+authEncoded);
 
        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(text.getBytes());
        os.flush();
        os.close();
        // For POST only - END
        
        logger.info("message = "+con.getResponseMessage());
 
        int responseCode = con.getResponseCode();
 
        if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
        	logger.error("POST request to InfluxDB has not worked: "+responseCode);
        } 
        
    }
    

}
