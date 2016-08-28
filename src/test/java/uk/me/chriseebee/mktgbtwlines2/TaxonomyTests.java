package uk.me.chriseebee.mktgbtwlines2;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.nlp.NamedEntityManager;

public class TaxonomyTests {

	Logger logger = LoggerFactory.getLogger(TaxonomyTests.class);
	NamedEntityManager tm;
	
	 @Before
	 public void before() throws Exception {
		 logger.info("Setting it up!");
    	tm = new NamedEntityManager();
    	tm.loadTaxonomy();
	 }

	@Test
	public void testListApproach() {
		List<String> testProducts = null;
		    	
        try {
            URI uri = this.getClass().getResource("testProducts.txt").toURI();
            testProducts = Files.readAllLines(Paths.get(uri),Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        int indx = testProducts.indexOf("jewellery");
        
        assertTrue(indx==3);
		    	
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
    
    
    
    
//    @After // tearDown()
//    public void after() throws Exception {
//    	System.out.println("Running: tearDown");
//    	dummyAccount = null;
//    	assertNull(dummyAccount);
//    }

}
