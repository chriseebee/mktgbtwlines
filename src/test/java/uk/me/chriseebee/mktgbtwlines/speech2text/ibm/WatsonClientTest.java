package uk.me.chriseebee.mktgbtwlines.speech2text.ibm;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.watson.developer_cloud.alchemy.v1.model.Sentiment;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;

import uk.me.chriseebee.mktgbtwlines.speech2text.TestFilesSetup;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.mappers.AppConfig;
import uk.me.chriseebee.mktgbtwlines2.nlp.ibm.AlchemyClient;

public class WatsonClientTest {
	
	Logger logger = LoggerFactory.getLogger(WatsonClientTest.class);
	AlchemyClient ac =null;
	
	@Before // setup()
	public void before() throws Exception {
		ac = new AlchemyClient();
		assertTrue(ac!=null);
	}

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
		
		String s = "I like car manufacturers Audi as well as BMW and Alfa Romeo";
		Sentiment sentiment = ac.getSentenceSentiment(s);
		logger.info(s + "\n"+sentiment.toString());
	}
	
	
	@Test
	public void testSentiment2() {
		
		String s = "I really love car manufacturers Audi as well as BMW and Alfa Romeo";
		Sentiment sentiment = ac.getSentenceSentiment(s);
		logger.info(s + "\n"+sentiment.toString());
	}
	
	@Test
	public void testSentiment3() {
		
		String s ="Car manufacturers Audi as well as BMW and Alfa Romeo are amazing, i love them";
		Sentiment sentiment = ac.getSentenceSentiment(s);
		logger.info(s + "\n"+sentiment.toString());
	}
	
	@Test
	public void testSentiment4() {
		
		String s = "Car manufacturers Audi as well as BMW and Alfa Romeo are awful, i hate them";
		Sentiment sentiment = ac.getSentenceSentiment(s);
		logger.info(s + "\n"+sentiment.toString());
	}
	
	@Test
	public void testSentiment5() {
		
		String s ="Car manufacturers Audi as well as BMW and Alfa Romeo are not good";
		Sentiment sentiment = ac.getSentenceSentiment(s);
		logger.info(s + "\n"+sentiment.toString());
	}
	
	
	@Test
	public void testSentiment6() {
		String s ="Car manufacturers Audi as well as BMW and Alfa Romeo are alright really";
		Sentiment sentiment = ac.getSentenceSentiment(s);
		logger.info(s + "\n"+sentiment.toString());
	}


}
