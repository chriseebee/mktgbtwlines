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
import uk.me.chriseebee.mktgbtwlines2.Utils;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;


public class NamedEntityManager {
	
	static Logger logger = LoggerFactory.getLogger(NamedEntityManager.class);
	
	public static String PRODUCT_TYPE = "P";
	public static String BRAND_TYPE = "B";
	
	private static String PRODUCTS = "/ner_products.txt";
	private static String BRANDS = "/ner_brands.txt";
	
	private static String PRODUCTS_FULL = "/ner_products_full_name.txt";
	private static String BRANDS_FULL = "/ner_brands_full_name.txt";
	
	private static String INTENTS = "/intents.txt";
	
	private static List<String> products = new ArrayList<String>();
	private static List<String> brands = new ArrayList<String>();
	private static List<String> productsFull = new ArrayList<String>();
	private static List<String> brandsFull = new ArrayList<String>();
	private static List<String> intents = new ArrayList<String>();
	
	//DB db = null;

	public static void setup() {
		
		//db = DBMaker.memoryDB().make();
		
		if (products.size()==0) {
			Utils.getLines(PRODUCTS, products);
			Utils.getLines(PRODUCTS_FULL, productsFull);
			Utils.getLines(BRANDS, brands);
			Utils.getLines(BRANDS_FULL, brandsFull);
			Utils.getLines(INTENTS, intents);
			
			logger.info("Product Count = "+products.size() );
			logger.info("Intent Count = "+intents.size() );
		}
	}
	
	
	
	public static List<String> getIntents() {
		return intents;
	}
	
	public static int getProductCount() {
		return products.size(); 
	}
	
	
	public static int getBrandCount() {
		return brands.size();
	}
	
	public static String getIntents(List<CoreLabel> words) {
		
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
	
	public static int getIndexOf(String type, String lookup) {
		int returnVal = -1;
		if (type.equals(PRODUCT_TYPE)) {
			returnVal = products.indexOf(lookup.toLowerCase());
		}
		
		if (type.equals(BRAND_TYPE)) {
			returnVal = brands.indexOf(lookup.toLowerCase());
		}
		
		return returnVal;
	}
	

	public static String isWordRecognized(String lookup) {
		
		/* TODO
		 This will have to change to accomodate a lookup of unique words in Orient
		 In Orient we will need to index all names of entities into a list similar to this memory list
		 so that we can call that instead of this local code.
		 
		 However, we could pull all the names out of Orient and do that here in mem. Could be faster
		 
		*/
		
		// first lets check the brand list
		if (brands.indexOf(lookup.toLowerCase())>0) {
			return BRAND_TYPE;
		}
		
		if (products.indexOf(lookup.toLowerCase())>0) {
			return PRODUCT_TYPE;
		}
		
		return null;
	}
	
	public static String isWordRecognized(String lookup, String nounType) {
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
	
	public static String isPhraseRecognized(String lookup, String nounType) {
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
