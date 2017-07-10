package uk.me.chriseebee.mktgbtwlines2.nlp;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NLUComparisonTester {

	Logger logger = LoggerFactory.getLogger(NLUComparisonTester.class);
	

	@Test
	public void test1() {
		String text = "HMRC is based in London";
		InfoExtractor.extractInfoFrom(text, "WATSON,DANDELION,STANFORD", "ENTITY");
	}
	


}
