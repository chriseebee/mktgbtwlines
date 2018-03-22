package uk.me.chriseebee.mktgbtwlines2.nlp;


import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;
import uk.me.chriseebee.mktgbtwlines.speech2text.ibm.WatsonClient;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;
import uk.me.chriseebee.mktgbtwlines2.nlp.InterestingEvent;
import uk.me.chriseebee.mktgbtwlines2.nlp.NLPPipeline;
import uk.me.chriseebee.mktgbtwlines2.nlp.Transcription;

public class NLPPipelineTest {
	
	static NLPPipeline pipe = null;
	static WatsonClient wc = null;
	
	@BeforeClass
	public static void setup() {
		
		try {
			ConfigLoader.getConfig();
			pipe = new NLPPipeline();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			WatsonClient.setup();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	@Test
	public void testMainPipeline0() {		
		Transcription t = new Transcription
		("I wish I could get a set of those things that I have wanted for a while. i need something which will be able to adapt to my voice as well as my wifes. " +
				"deciding which is the nicest shirt is really challenging. we would very much like to get our floor resurfaced. Its amazing what you can buy these days. " +
				"producing a list of products and brands is quite hard. why do humans have to speak in such complicated ways. under what circumstances is it acceptable to punch your developers. a lot of people ask me about my expensive cars and jeWEllery"
				,new Date());
		pipe.processText(t);
	}
	
	@Test
	public void testMainPipeline1() {
		Transcription t = new Transcription
		("three brands does axe and Old Spice have generated tremendous consumer interests and identification in a historical low involvement category when you would never expect to get attention on social media",new Date());
		pipe.processText(t);
	}
	
	@Test
	public void testMainPipeline2() {
		Transcription t = new Transcription
		("the idea that consumers could possibly want to talk about Corona or cause in the same way that they debate the Talents of Ronaldo and Messi is silly",new Date());
		pipe.processText(t);
	}
	
	@Test
	public void testMainPipeline3a() {
		
		Transcription t = new Transcription
		("I really love car manufacturers Audi as well as BMW and Alfa Romeo",new Date());
		pipe.processText(t);
	}
	
	@Test
	public void testMainPipeline3b() {
		Transcription t = new Transcription
		("Car manufacturers Audi as well as BMW and Alfa Romeo are amazing, i love them",new Date());
		try {
			pipe.processText(t);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testMainPipeline4a() {
		Transcription t = new Transcription
		("what is hard is that Gucci and Louis Vuitton a tricky names to capture",new Date());
		pipe.processText(t);
	}
	
	
	@Test
	public void testMainPipeline4b() {
		Transcription t = new Transcription
		("I am most fond of the handbags produced by Louis Vuitton",new Date());
		pipe.processText(t);
	}
	
	@Test
	public void testMainPipeline5() {
		Transcription t = new Transcription
		("I do most of my food shopping at Marks and Spencer these days but in the US Walmart is a huge retailer",new Date());
		pipe.processText(t);
	}
	
	@Test
	public void testMainPipeline6() {
		Transcription t = new Transcription
		("I'm not sure about Starbucks coffee or that from Caffe Nero but Carluccio's is great",new Date());
		pipe.processText(t);
	}

	@Test
	public void testMainPipeline7() {
		Transcription t = new Transcription
		("I'm not sure about Starbucks coffee",new Date());
		pipe.processText(t);
	}
	
	@Test
	public void testMainPipeline8() {
		Transcription t = new Transcription
		("Sony Televisions have always been my preference over bang and olufsen, Panasonic and Philips",new Date());
		pipe.processText(t);
	}
	*/
	@Test
	public void testEntityRemoval1() {
		// Simple test with 1 single word entity occuring once
		CoreMap sentence = getSentence("I wanted to go and see Robert for a few drinks");
		System.out.println("SENTENCE: "+sentence);
		AnalysisResults ar = WatsonClient.process(sentence.toString(),null);
		List<InterestingEvent> ieList = WatsonClient.mapEntities(ar);
		
		List<CoreLabel> words = sentence.get(TokensAnnotation.class);
		
		String wordAtPos7Before = words.get(6).originalText();
		System.out.println("Word Before = "+wordAtPos7Before);
		
		try {
			pipe.removeKnownEntitiesFromSentence(sentence.toString(),words, ieList);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		String wordAtPos7After = words.get(6).originalText();
		System.out.println("Word After = "+wordAtPos7After);
		
		if (!wordAtPos7Before.equals("Robert") || !wordAtPos7After.equals("for")) {
			fail("Error");
		} else {
			assertTrue("Sucess!",true);
		}
	}
	
	@Test
	public void testEntityRemoval2() {
		// Simple test with 2 single word entity occuring once
		CoreMap sentence = getSentence("I wanted to go and see Robert for a few drinks with Mary and her dog");
		System.out.println("SENTENCE: "+sentence);
		AnalysisResults ar = WatsonClient.process(sentence.toString(),null);
		List<InterestingEvent> ieList = WatsonClient.mapEntities(ar);
		
		List<CoreLabel> words = sentence.get(TokensAnnotation.class);
		
		String wordAtPos13Before = words.get(12).originalText();
		System.out.println("Word Before = "+wordAtPos13Before);
		
		try {
			pipe.removeKnownEntitiesFromSentence(sentence.toString(),words, ieList);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		String wordAtPos13After = words.get(12).originalText();
		System.out.println("Word After = "+wordAtPos13After);
		
		if (!wordAtPos13Before.equals("Mary") || !wordAtPos13After.equals("her")) {
			fail("Error");
		} else {
			assertTrue("Sucess!",true);
		}
	}
	
	@Test
	public void testEntityRemoval3() {
		// multi-word entity occuring once
		CoreMap sentence = getSentence("I wanted to buy a Louis Vuitton handbag tomorrow");
		System.out.println("SENTENCE: "+sentence);
		AnalysisResults ar = WatsonClient.process(sentence.toString(),null);
		List<InterestingEvent> ieList = WatsonClient.mapEntities(ar);
		
		List<CoreLabel> words = sentence.get(TokensAnnotation.class);
		
		String wordAtPos7Before = words.get(6).originalText();
		System.out.println("Word Before = "+wordAtPos7Before);
		
		try {
			pipe.removeKnownEntitiesFromSentence(sentence.toString(),words, ieList);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		String wordAtPos7After = words.get(6).originalText();
		System.out.println("Word After = "+wordAtPos7After);
		
		if (!wordAtPos7Before.equals("Vuitton") || !wordAtPos7After.equals("tomorrow")) {
			fail("Error");
		} else {
			assertTrue("Sucess!",true);
		}
	}
	
	@Test
	public void testEntityRemoval4() {
		// multi-word entity occuring once and multiple single word entities
		CoreMap sentence = getSentence("I wanted to buy a Louis Vuitton handbag tomorrow to give to Lucy for her birthday before Arthur finds out");
		System.out.println("SENTENCE: "+sentence);
		AnalysisResults ar = WatsonClient.process(sentence.toString(),null);
		List<InterestingEvent> ieList = WatsonClient.mapEntities(ar);
		
		List<CoreLabel> words = sentence.get(TokensAnnotation.class);
		
		String wordAtPos18Before = words.get(17).originalText();
		System.out.println("Word at 18 Before = "+wordAtPos18Before);
		
		try {
			pipe.removeKnownEntitiesFromSentence(sentence.toString(),words, ieList);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		String wordAtPos16After = words.get(15).originalText();
		System.out.println("Word at 16 After = "+wordAtPos16After);
		
		if (!wordAtPos18Before.equals("Arthur") || !wordAtPos16After.equals("out")) {
			fail("Error");
		} else {
			assertTrue("Sucess!",true);
		}
	}
	
	private CoreMap getSentence(String text) {
		Transcription t = new Transcription(text,new Date());
		List<CoreMap> sentences = null;
		try {
			sentences = pipe.getSentences(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
		CoreMap sentence = sentences.get(0);
		return sentence;
	}
	
	
}
