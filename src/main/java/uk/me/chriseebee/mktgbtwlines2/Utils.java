package uk.me.chriseebee.mktgbtwlines2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Utils {
	
	static Logger logger = LoggerFactory.getLogger(Utils.class);

	public Utils() {
		// TODO Auto-generated constructor stub
	}

	
	public static void getLines(String resourceName, List<String> list) {

		BufferedReader r = new BufferedReader(new InputStreamReader((Utils.class.getResourceAsStream(resourceName))));

	    String inLine; //Buffer to store the current line
	    try {
			while ((inLine = r.readLine()) != null) //Read line-by-line, until end of file
			{
				//logger.info("Adding" + inLine);
			    list.add(inLine);
			}
		} catch (IOException e) {
			logger.error("Could not load  File",e);
		}
	    try {
			r.close();
		} catch (IOException e) {
			logger.error("Could not load  File",e);
		} 
	    
	   
	}
}
