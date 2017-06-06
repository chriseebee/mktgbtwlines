package uk.me.chriseebee.mktgbtwlines.speech2text.ibm;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.watson.developer_cloud.alchemy.v1.model.Sentiment;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.stats.PrecisionRecallStats;

import uk.me.chriseebee.mktgbtwlines.speech2text.TestFilesSetup;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;
import uk.me.chriseebee.mktgbtwlines2.config.mappers.AppConfig;
import uk.me.chriseebee.mktgbtwlines2.nlp.InterestingEvent;
import uk.me.chriseebee.mktgbtwlines2.nlp.entity.StanfordNerClient;
import uk.me.chriseebee.mktgbtwlines2.nlp.ibm.AlchemyClient;

public class WatsonClientTest {
	
	Logger logger = LoggerFactory.getLogger(WatsonClientTest.class);
	
	WatsonClient wc = null;
	
	String text = "This is a sentence about handbags and cars owned by Chris Bell and bought from Prada and BMW. This sentence is about tables and a chair that I got from Heals which has a nice vase on top which i got from  a guy called Roger in Manchester, which is made by Denby.";
	Annotation document = null;
	StanfordCoreNLP pipeline = null;
	
	@Before
	public void setup() {
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, parse, depparse");
		pipeline = new StanfordCoreNLP(props);
		
		try {
			wc = new WatsonClient();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail();
		}

	}

	/*
	@Test
	public void test() {
		
		TestFilesSetup tfs = new TestFilesSetup();
		AppConfig ac = null; 
		try {
			ConfigLoader cl = ConfigLoader.getConfigLoader();
			ac = cl.getConfig();
		} catch (Exception e) {
			e.printStackTrace();
			fail("BIG ERROR LOADING CONFIG");
			return;
		}
		
		//System.out.println(ac.getWatsonKeys().get("username") + "/" +ac.getWatsonKeys().get("password"));
		
		WatsonClient wc = new WatsonClient(
				 ac.getWatsonKeys().get("username")
				,ac.getWatsonKeys().get("password")
				);
		
		for (int i=0;i<6;i++) {
			logger.info("File "+i);
			URI uri = null;
			try {
				uri = this.getClass().getResource("/"+tfs.getFileNameList().get(i)).toURI();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SpeechResults res = wc.processFile(new File(uri));
			for (Transcript t: res.getResults()) {
				if (t.isFinal()) {
					logger.info(t.getAlternatives().get(0).getTranscript());
				}
			}
		}

	}
	
	
	@Test
	public void testSentiment1() {
		ConfigLoader.getConfigLoader("config.yml");
		
		AlchemyClient ac = null;
		try {
			ac = new AlchemyClient();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail();
		}
		String s = "I like car manufacturers Audi as well as BMW and Alfa Romeo";
		Sentiment sentiment = ac.getSentenceSentiment(s);
		logger.info(s + "\n"+sentiment.toString());
	}
	
	
	@Test
	public void testSentiment2() {
		ConfigLoader.getConfigLoader("config.yml");
		AlchemyClient ac = null;
		try {
			ac = new AlchemyClient();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail();
		}
		String s = "I really love car manufacturers Audi as well as BMW and Alfa Romeo";
		Sentiment sentiment = ac.getSentenceSentiment(s);
		logger.info(s + "\n"+sentiment.toString());
	}
	
	@Test
	public void testSentiment3() {
		ConfigLoader.getConfigLoader("config.yml");
		AlchemyClient ac = null;
		try {
			ac = new AlchemyClient();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail();
		}
		String s ="Car manufacturers Audi as well as BMW and Alfa Romeo are amazing, i love them";
		Sentiment sentiment = ac.getSentenceSentiment(s);
		logger.info(s + "\n"+sentiment.toString());
	}
	
	@Test
	public void testSentiment4() {
		ConfigLoader.getConfigLoader("config.yml");
		AlchemyClient ac = null;
		try {
			ac = new AlchemyClient();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail();
		}
		String s = "Car manufacturers Audi as well as BMW and Alfa Romeo are awful, i hate them";
		Sentiment sentiment = ac.getSentenceSentiment(s);
		logger.info(s + "\n"+sentiment.toString());
	}
	
	@Test
	public void testSentiment5() {
		ConfigLoader.getConfigLoader("config.yml");
		AlchemyClient ac = null;
		try {
			ac = new AlchemyClient();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail();
		}
		String s ="Car manufacturers Audi as well as BMW and Alfa Romeo are not good";
		Sentiment sentiment = ac.getSentenceSentiment(s);
		logger.info(s + "\n"+sentiment.toString());
	}
	
	
	@Test
	public void testSentiment6() {
		ConfigLoader.getConfigLoader("config.yml");
		AlchemyClient ac = null;
		try {
			ac = new AlchemyClient();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail();
		}
		String s ="Car manufacturers Audi as well as BMW and Alfa Romeo are alright really";
		Sentiment sentiment = ac.getSentenceSentiment(s);
		logger.info(s + "\n"+sentiment.toString());
	}
	*/


	@Test
	public void test1() {
		
		String text = "This is a sentence about handbags and cars owned by Chris Bell and bought from Prada and Ford. This sentence is about tables and a chair that I got from Ikea which has a nice vase on top which i got from  a guy called Roger in Manchester, which is made by Top Shop.";
		Map<String, String> m = Collections.synchronizedMap(new HashMap<String, String>());
		m.put("Chris", "Person");
		m.put("Bell", "Person");
		m.put("Prada", "Company");
		m.put("Ford", "Company");
		m.put("Ikea", "Company");
		m.put("Roger", "Person");
		m.put("Manchester", "Location");
		m.put("Top Shop", "Company");
		
		PrecisionRecallStats prs = testSentences(text, m);
		
		if (prs.getFMeasure() < 0.5) {
			fail("F-Score = "+prs.getFMeasure());
		} else {
			assertTrue("F-Score = "+ prs.getFMeasure(),true);
		}
	}
	
	
	@Test
	public void test2() {
		
		String text = "These people, Terry, Gita, Yingshun and Mohammed, loved going to the Odeon cinema with Sady Smith and Harris";
		Map<String, String> m = Collections.synchronizedMap(new HashMap<String, String>());
		
		logger.info(text);
		
		m.put("Terry", "Person");
		m.put("Gita", "Person");
		m.put("Yingshun", "Person");
		m.put("Mohammed", "Person");
		m.put("Sady Smith", "Person");
		m.put("Harris", "Person");
		m.put("Odeon", "Company");
		
		PrecisionRecallStats prs = testSentences(text, m);
		
		if (prs.getFMeasure() < 0.5) {
			fail("F-Score = "+prs.getFMeasure());
		} else {
			assertTrue("F-Score = "+ prs.getFMeasure(),true);
		}
	}
	
	@Test
	public void test2lowercase() {
		
		String text = "These people, terry, gita, Yingshun and Mohammed, loved going to the Odeon cinema with Sady Smith and Harris";
		Map<String, String> m = Collections.synchronizedMap(new HashMap<String, String>());
		
		logger.info(text);
		
		m.put("terry", "Person");
		m.put("gita", "Person");
		m.put("Yingshun", "Person");
		m.put("Mohammed", "Person");
		m.put("Sady Smith", "Person");
		m.put("Harris", "Person");
		m.put("Odeon", "Company");
		
		PrecisionRecallStats prs = testSentences(text, m);
		
		if (prs.getFMeasure() < 0.5) {
			fail("F-Score = "+prs.getFMeasure());
		} else {
			assertTrue("F-Score = "+ prs.getFMeasure(),true);
		}
	}
	
	@Test
	public void test3() {
		
		String text = "Identifying multi word entities such as Costa Coffee and Pret a Manger is really tricky";
		Map<String, String> m = Collections.synchronizedMap(new HashMap<String, String>());
		m.put("Costa Coffee", "Company");
		m.put("Pret a Manger", "Company");
		
		PrecisionRecallStats prs = testSentences(text, m);
		
		if (prs.getFMeasure() < 0.5) {
			fail("F-Score = "+prs.getFMeasure());
		} else {
			assertTrue("F-Score = "+ prs.getFMeasure(),true);
		}
	}

	private PrecisionRecallStats testSentences(String text, Map<String,String> m) {

		PrecisionRecallStats prs = new PrecisionRecallStats();
		
		document = new Annotation(text);
		// run all Annotators on this text
		pipeline.annotate(document);
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		for(CoreMap sentence: sentences) {
			
			logger.info("Sentence: "+sentence);
			
			System.out.println(sentence);
			AnalysisResults ar = wc.getEntities(sentence.toString());
			List<InterestingEvent> ieList = null;
			try {
				ieList = wc.mapEntities(ar);
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<CoreLabel> words = sentence.get(TokensAnnotation.class);
			
			for (InterestingEvent ie: ieList) {
				
				String originalWord = ie.getEntity().getName();
				String entityType = ie.getEntity().getType();
				
				String expectedType = m.get(originalWord);
				if (expectedType==null) {
					prs.addFP(1);
					System.out.println("Annotation Fail - not in test data: "+originalWord);
				} else {
					if (expectedType.equalsIgnoreCase(entityType)) {
						System.out.println("Annotation Pass");
						prs.addTP(1);
						m.remove(originalWord);
					} else {
						prs.addFP(1);
						System.out.println("Annotation Fail - missclassified: "+originalWord + "/" + expectedType + "/" + entityType);
					}
				}
			}		
			
		}
		
		prs.addFN(m.size());
	
		System.out.println("Precision = "+prs.getPrecision());
		System.out.println("Recall = "+prs.getRecall());
		System.out.println("F-Score = "+prs.getFMeasure());
		return prs;

	}
}
