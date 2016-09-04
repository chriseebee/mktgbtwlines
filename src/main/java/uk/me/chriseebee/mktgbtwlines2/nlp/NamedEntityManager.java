package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;


public class NamedEntityManager {
	
	Logger logger = LoggerFactory.getLogger(NamedEntityManager.class);
	
	public static String PRODUCT_TYPE = "P";
	public static String BRAND_TYPE = "B";
	
	private static String PRODUCTS = "/ner_products.txt";
	private static String BRANDS = "/ner_brands.txt";
	
	private static String PRODUCTS_FULL = "/ner_products_full_name.txt";
	private static String BRANDS_FULL = "/ner_brands_full_name.txt";
	
	private static String INTENTS = "/intents.txt";
	
	private List<String> products = new ArrayList<String>();
	private List<String> brands = new ArrayList<String>();
	private List<String> productsFull = new ArrayList<String>();
	private List<String> brandsFull = new ArrayList<String>();
	private List<String> intents = new ArrayList<String>();

	public NamedEntityManager() {
		getLines(PRODUCTS, products);
		getLines(PRODUCTS_FULL, productsFull);
		getLines(BRANDS, brands);
		getLines(BRANDS_FULL, brandsFull);
		getLines(INTENTS, intents);
		
		logger.info("Product Count = "+products.size() );
		logger.info("Intent Count = "+intents.size() );
	}
	
	private void getLines(String resourceName, List<String> list) {

		BufferedReader r = new BufferedReader(new InputStreamReader((NamedEntityManager.class.getResourceAsStream(resourceName))));

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
	
	public List<String> getIntents() {
		return intents;
	}
	
	public int getProductCount() {
		return products.size(); 
	}
	
	
	public int getBrandCount() {
		return brands.size();
	}
	
	public String getIntents(List<CoreLabel> words) {
		
		List<String> matches = new ArrayList<String>();
		
		for (CoreLabel word : words) {
			
			String wt = word.get(TextAnnotation.class);
			logger.trace("Finding "+wt+" in intents");
			int idx = intents.indexOf(wt.toLowerCase());
			logger.trace("Index = "+idx);
			if (idx>0) { matches.add(wt); }
		}
		
		List<String> listWithoutDuplicates = new ArrayList<>(new HashSet<>(matches));
		String csv = String.join(",", listWithoutDuplicates);
		if (csv == null) { csv = "empty"; }
		logger.debug("Intents = "+csv);
		return csv;
	}
	
	public int getIndexOf(String type, String lookup) {
		int returnVal = -1;
		if (type.equals(PRODUCT_TYPE)) {
			returnVal = products.indexOf(lookup.toLowerCase());
		}
		
		if (type.equals(BRAND_TYPE)) {
			returnVal = brands.indexOf(lookup.toLowerCase());
		}
		
		return returnVal;
	}
	
	public String isWordRecognized(String lookup) {
		// first lets check the brand list
		if (brands.indexOf(lookup.toLowerCase())>0) {
			return BRAND_TYPE;
		}
		
		if (products.indexOf(lookup.toLowerCase())>0) {
			return PRODUCT_TYPE;
		}
		
		return null;
	}
	
	public String isWordRecognized(String lookup, String nounType) {
		// first lets check the brand list
		if (nounType==null || nounType.equals(BRAND_TYPE)) {
			if (brands.indexOf(lookup.toLowerCase())>0) {
				return BRAND_TYPE;
			}
		} 
		
		if (nounType==null || nounType.equals(PRODUCT_TYPE)) {
			logger.info("Testing "+lookup+" against the list");
			int indx = products.indexOf(lookup.toLowerCase());
			logger.info("Index is : "+indx);
			if (indx>0) {
				return PRODUCT_TYPE;	
			}
		}
		return null;
	}
	
	public String isPhraseRecognized(String lookup, String nounType) {
		// first lets check the brand list
		if (nounType==null || nounType.equals(BRAND_TYPE)) {
			if (brandsFull.indexOf(lookup.toLowerCase())>0) {
				return BRAND_TYPE;
			}
		}
		
		if (nounType==null || nounType.equals(PRODUCT_TYPE)) {
			
			if (productsFull.indexOf(lookup.toLowerCase())>0) {
				return PRODUCT_TYPE;
			}
		}
		return null;
	}
	
}
