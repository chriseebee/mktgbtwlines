package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	
	List<String> products;
	List<String> brands;
	List<String> productsFull;
	List<String> brandsFull;
	

	public NamedEntityManager() {
		loadTaxonomy();
	}
	
	public void loadTaxonomy() {

        try {
            URI uri = this.getClass().getResource(PRODUCTS).toURI();
            products = Files.readAllLines(Paths.get(uri),Charset.defaultCharset());
        } catch (Exception e) {
        	logger.error("Could not load Products File",e);
        }
        
        try {
            URI uri = this.getClass().getResource(PRODUCTS_FULL).toURI();
            productsFull = Files.readAllLines(Paths.get(uri),Charset.defaultCharset());
        } catch (Exception e) {
        	logger.error("Could not load Products Full File",e);
        }
        
        try {
            URI uri = this.getClass().getResource(BRANDS).toURI();
            brands = Files.readAllLines(Paths.get(uri),Charset.defaultCharset());
        } catch (Exception e) {
        	logger.error("Could not load Brands File",e);
        }
        
        try {
            URI uri = this.getClass().getResource(BRANDS_FULL).toURI();
            brandsFull = Files.readAllLines(Paths.get(uri),Charset.defaultCharset());
        } catch (Exception e) {
        	logger.error("Could not load Brands Full File",e);
        }

	}
	
	public int getProductCount() {
		return products.size();
	}
	
	
	public int getBrandCount() {
		return brands.size();
	}
	
	public int getIndexOf(String type, String lookup) {
		int returnVal = -1;
		if (type.equals(PRODUCT_TYPE)) {
			returnVal = products.indexOf(lookup);
		}
		
		if (type.equals(BRAND_TYPE)) {
			returnVal = brands.indexOf(lookup);
		}
		
		return returnVal;
	}
	
	public String isWordRecognized(String lookup) {
		// first lets check the brand list
		if (brands.indexOf(lookup)>0) {
			return BRAND_TYPE;
		}
		
		if (products.indexOf(lookup)>0) {
			return PRODUCT_TYPE;
		}
		
		return null;
	}
	
	public String isWordRecognized(String lookup, String nounType) {
		// first lets check the brand list
		if (nounType==null || nounType.equals(BRAND_TYPE)) {
			if (brands.indexOf(lookup)>0) {
				return BRAND_TYPE;
			}
		}
		
		if (nounType==null || nounType.equals(PRODUCT_TYPE)) {
			logger.info("Testing "+lookup+" against the list");
			int indx = products.indexOf(lookup);
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
			if (brandsFull.indexOf(lookup)>0) {
				return BRAND_TYPE;
			}
		}
		
		if (nounType==null || nounType.equals(PRODUCT_TYPE)) {
			
			if (productsFull.indexOf(lookup)>0) {
				return PRODUCT_TYPE;
			}
		}
		return null;
	}
}
