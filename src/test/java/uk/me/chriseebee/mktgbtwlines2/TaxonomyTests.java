package uk.me.chriseebee.mktgbtwlines2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TaxonomyTests {


    @Test
    public void testTaxonomyBootstrap()
    {
    	NamedEntityManager tm;
    	tm = new NamedEntityManager();
    	tm.loadTaxonomy();
    	
        assertTrue(true);
    }
    
    @Test
    public void testProductLoad()
    {
    	NamedEntityManager tm;
    	tm = new NamedEntityManager();
    	tm.loadTaxonomy();
    	int prodCount = tm.getProductCount();
    	System.out.println("Number of Products = "+prodCount);
    	
        assertTrue( prodCount>0 );
    }
    
    @Test
    public void testBrandLoad()
    {
    	NamedEntityManager tm;
    	tm = new NamedEntityManager();
    	tm.loadTaxonomy();
    	int brandCount = tm.getProductCount();
    	System.out.println("Number of Brands = "+brandCount);
    	
        assertTrue( brandCount>0 );
    }
}
