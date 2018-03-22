package uk.me.chriseebee.mktgbtwlines2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Utils {
	
	static Logger logger = LoggerFactory.getLogger(Utils.class);
	
	public static void getLines(String resourceName, List<String> list) {

		File f  = new File(resourceName);
		
		FileReader fr = null;
		
		try {
			fr = new FileReader(f);
			
			BufferedReader r = new BufferedReader(fr);

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
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
