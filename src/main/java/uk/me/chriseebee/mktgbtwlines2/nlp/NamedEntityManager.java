package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NamedEntityManager {
	
	Logger logger = LoggerFactory.getLogger(NamedEntityManager.class);
	
	public static String PRODUCT_TYPE = "P";
	public static String BRAND_TYPE = "B";
	
	private static String PRODUCTS = "/ner_products.txt";
	private static String BRANDS = "/ner_brands.txt";
	
	private static String PRODUCTS_FULL = "/ner_products_full_name.txt";
	private static String BRANDS_FULL = "/ner_brands_full_name.txt";
	
	private List<String> products = new ArrayList<String>();;
	private List<String> brands = new ArrayList<String>();;
	private List<String> productsFull = new ArrayList<String>();;
	private List<String> brandsFull = new ArrayList<String>();;
	

	public NamedEntityManager() {
		getLines(PRODUCTS, products);
		getLines(PRODUCTS_FULL, productsFull);
		getLines(BRANDS, brands);
		getLines(BRANDS_FULL, brandsFull);
		
		 logger.info("Product Count = "+products.size() );
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
	
//	public void loadTaxonomy() {


		
//        try {
//            URI uri = this.getClass().getResource(PRODUCTS).toURI();
//            products = Files.readAllLines(Paths.get(uri),Charset.defaultCharset());
//        } catch (Exception e) {
//        	logger.error("Could not load Products File",e);
//        }
//        

//	}
	
	public int getProductCount() {
		return products.size(); 
	}
	
	
	public int getBrandCount() {
		return brands.size();
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
