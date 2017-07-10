package uk.me.chriseebee.mktgbtwlines2.nlp;

import static org.junit.Assert.*;
import semantics.Compare;

import org.junit.Test;

public class SemanticSimilarityTest {

	@Test
	public void test1() {
		String a = "ORG serves FOOD_TYPE food";
	    String b = "FOOD_TYPE food is served at the ORG";

	    Compare c = new Compare(a,b);
	    System.out.println("Similarity between the sentences\n-"+a+"\n-"+b+"\n is: " + c.getResult());

	}
	
	@Test
	public void test2() {
		String a = "ORG is located at LOC";
	    String b = "ORG is found at LOC";

	    Compare c = new Compare(a,b);
	    System.out.println("Similarity between the sentences\n-"+a+"\n-"+b+"\n is: " + c.getResult());

	}

	@Test
	public void test3() {
		String a = "LOC based ORG";
	    String b = "ORG is found at LOC";

	    Compare c = new Compare(a,b);
	    System.out.println("Similarity between the sentences\n-"+a+"\n-"+b+"\n is: " + c.getResult());

	}
	
	@Test
	public void test4() {
		String a = "ORG is not found at LOC";
	    String b = "ORG is found at LOC";

	    Compare c = new Compare(a,b);
	    System.out.println("Similarity between the sentences\n-"+a+"\n-"+b+"\n is: " + c.getResult());

	}
	
}
