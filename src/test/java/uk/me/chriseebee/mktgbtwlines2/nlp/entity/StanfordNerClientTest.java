package uk.me.chriseebee.mktgbtwlines2.nlp.entity;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class StanfordNerClientTest {

	String text = "This is a sentence about handbags and cars owned by Chris Bell and bought from Prada and BMW. This sentence is about tables and a chair that I got from Heals which has a nice vase on top which i got from  a guy called Roger in Manchester, which is made by Denby.";
	Annotation document = null;
	static StanfordCoreNLP pipeline = null;
	
	@BeforeClass
	public static void setup() {
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, parse, depparse, truecase");
		pipeline = new StanfordCoreNLP(props);

	}
	
	@Test
	public void test1() {
		
		String text = "This is a sentence about handbags and cars owned by Chris Bell and bought from Prada and BMW. This sentence is about tables and a chair that I got from Heals which has a nice vase on top which i got from  a guy called Roger in Manchester, which is made by Denby.";
		Map<String, String> m = Collections.synchronizedMap(new HashMap<String, String>());
		m.put("Chris", "PERSON");
		m.put("Bell", "PERSON");
		m.put("Prada", "ORGANIZATION");
		m.put("BMW", "ORGANIZATION");
		m.put("Heals", "ORGANIZATION");
		m.put("Roger", "PERSON");
		m.put("Manchester", "LOCATION");
		m.put("Denby", "ORGANIZATION");
		
		double passRate = testSentences(text, m);
		
		if (passRate < 0.5) {
			fail("Sucess Ratio = "+passRate);
		} else {
			assertTrue("Sucess Ratio= "+ passRate,true);
		}
	}
	
	
	@Test
	public void test2() {
		
		String text = "These people, Terry, Gita, Yingshun and Mohammed, loved going to the Odeon cinema with Sady Smith and Harris";
		Map<String, String> m = Collections.synchronizedMap(new HashMap<String, String>());
		m.put("Terry", "PERSON");
		m.put("Gita", "PERSON");
		m.put("Yingshun", "PERSON");
		m.put("Mohammed", "PERSON");
		m.put("Sady", "PERSON");
		m.put("Smith", "PERSON");
		m.put("Harris", "PERSON");
		m.put("Odeon", "ORGANIZATION");
		
		double passRate = testSentences(text, m);
		
		if (passRate < 0.5) {
			fail("Sucess Ratio = "+passRate);
		} else {
			assertTrue("Sucess Ratio= "+ passRate,true);
		}
	}
	
	
	@Test
	public void test2lowercase() {
		
		String text = "These people, terry, gita, yingshun and mohammed, loved going to the manchester odeon cinema with Sady Smith and Harris, but they didn't go to carlisle";
		Map<String, String> m = Collections.synchronizedMap(new HashMap<String, String>());
		m.put("terry", "PERSON");
		m.put("gita", "PERSON");
		m.put("yingshun", "PERSON");
		m.put("mohammed", "PERSON");
		m.put("sady", "PERSON");
		m.put("smith", "PERSON");
		m.put("harris", "PERSON");
		m.put("odeon", "ORGANIZATION");
		m.put("carlisle", "LOCATION");
		m.put("manchester", "LOCATION");
		
		double passRate = testSentences(text, m);
		
		if (passRate < 0.5) {
			fail("Sucess Ratio = "+passRate);
		} else {
			assertTrue("Sucess Ratio= "+ passRate,true);
		}
	}
	
	@Test
	public void test10lowercase() {
		
		String text = "My name is John and i like things like Table Noodle";
		Map<String, String> m = Collections.synchronizedMap(new HashMap<String, String>());
		m.put("table", "PERSON");
		m.put("noodle", "PERSON");
		
		double passRate = testSentences(text, m);
		
		if (passRate < 0.5) {
			fail("Sucess Ratio = "+passRate);
		} else {
			assertTrue("Sucess Ratio= "+ passRate,true);
		}
	}
	
	@Test
	public void test11() {
		
		String text = "My name is Table Noodle";
		Map<String, String> m = Collections.synchronizedMap(new HashMap<String, String>());
		m.put("Table", "PERSON");
		m.put("Noodle", "PERSON");
		
		double passRate = testSentences(text, m);
		
		if (passRate < 0.5) {
			fail("Sucess Ratio = "+passRate);
		} else {
			assertTrue("Sucess Ratio= "+ passRate,true);
		}
	}


	private double testSentences(String text, Map<String,String> m) {
		int successCount = 0;
		
		document = new Annotation(text);
		// run all Annotators on this text
		pipeline.annotate(document);
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		StanfordNerClassifierClient client = new StanfordNerClassifierClient();
		
		for(CoreMap sentence: sentences) {
			System.out.println(sentence);
			
			List<CoreLabel> words = sentence.get(TokensAnnotation.class);
			
			List<CoreLabel> entities = client.getEntities(words);
			
			for (CoreLabel l: entities) {
				//if (l.get(CoreAnnotations.PartOfSpeechAnnotation.class).startsWith("NN")) {
					System.out.println(l.originalText()+ ":"+ l.get(CoreAnnotations.PartOfSpeechAnnotation.class)+ ":"+ l.get(CoreAnnotations.AnswerAnnotation.class));
					if (l.get(CoreAnnotations.AnswerAnnotation.class).equals(m.get(l.originalText()))) {
						System.out.println("Annotation Pass");
						successCount++;
					} else {
						System.out.println("Annotation Fail");
					}
				//}
			}
		}
	
		double passRate = (double)successCount /  (double)m.size();
		System.out.println("Success Ratio = "+passRate);
		return passRate;
	}
}
