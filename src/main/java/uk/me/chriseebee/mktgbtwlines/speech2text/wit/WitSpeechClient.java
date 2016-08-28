package uk.me.chriseebee.mktgbtwlines.speech2text.wit;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.common.io.Files;

public class WitSpeechClient {
	URL obj;
    

	
    private void sendAudioFile(String path) throws IOException {
        
        File filetosend = new File(path);
        byte[] bytearray = null;
        
    	try {
			obj = new URL("https://api.wit.ai/speech?v=20160810");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer <<TOKEN>>");
        con.setRequestProperty("Content-Type", "audio/wav");
        // con.setFollowRedirects(true); THIS IS STATIC AND DEFAULT TRUE, JUST ADDING FOR EXPLANATION
 
        // For POST only - START
        con.setDoOutput(true);
        
        OutputStream os = con.getOutputStream();
        os.write(bytearray);
        os.flush();
        os.close();
        // For POST only - END
 
        int responseCode = con.getResponseCode();
 
        if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
        	System.out.println("POST request to InfluxDB has not worked: "+responseCode);
        } 
        
    }
}
