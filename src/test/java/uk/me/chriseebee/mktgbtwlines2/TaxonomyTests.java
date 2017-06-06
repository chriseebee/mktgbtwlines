package uk.me.chriseebee.mktgbtwlines2;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.nlp.NamedEntityManager;

public class TaxonomyTests {

	static Logger logger = LoggerFactory.getLogger(TaxonomyTests.class);
	static NamedEntityManager tm;
	
	 @BeforeClass
	 public static void before() throws Exception {
		 logger.info("Setting it up!");
    	tm = new NamedEntityManager();
	 }
    
    @Test
    public void testIntentLoad()
    {
    
    	List<String> intents = tm.getIntents();
    	int idx = intents.indexOf("love");

        assertTrue( idx>0 );
    }
	 
    @Test
    public void testProductLoad()
    {
    
    	int prodCount = tm.getProductCount();
    	logger.info("Number of Products = "+prodCount);

        assertTrue( prodCount>0 );
    }
    
    @Test
    public void testBrandLoad() 
    {
    	int brandCount = tm.getBrandCount();
    	logger.info("Number of Brands = "+brandCount);
    	
        assertTrue( brandCount>0 );
    }
    
    @Test
    public void testJewelleryIsProduct() {
    	String type = tm.isWordRecognized("jewellery",null);
        assertTrue( type.equals("P")); 	
    }
    
    @Test
    public void testVuittonIsBrand() {
    	String type = tm.isWordRecognized("vuitton",null);
        assertTrue( type.equals("B")); 	
    }
    
    

}
