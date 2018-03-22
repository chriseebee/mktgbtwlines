package uk.me.chriseebee.mktgbtwlines2.nlp.dandelion;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;

import uk.me.chriseebee.mktgbtwlines.speech2text.ibm.WatsonClient;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;

public class DandelionClientTest {

	@BeforeClass
	public static void setup() {
		try {
			DandelionClient.setup();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void test1() {
		doRun("Arcadia is a big company");
	}
	
	@Test
	public void test2() {
		doRun("I went to Costa Coffee in Carlisle where i saw an advert for The Odeon cinema");
	}
	
	@Test
	public void test3() {
		doRun("I went to Costa Coffee in Carlisle where i saw an advert for The Odeon cinema");
	}
	
	private void doRun(String text) {
		String annotations = "RELATION,CONCEPT,ENTITY,CATEGORY,KEYWORD";
		
		AnalysisResults ar = DandelionClient.process(text,Arrays.asList(annotations.split(",")));
		WatsonClient.printConfidentResults(ar);
	}

}
