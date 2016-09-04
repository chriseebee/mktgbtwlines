package uk.me.chriseebee.mktgbtwlines2;


import java.util.Date;

import org.junit.Test;

import uk.me.chriseebee.mktgbtwlines2.nlp.NLPPipeline;
import uk.me.chriseebee.mktgbtwlines2.nlp.Transcription;

public class NLPPipelineTest {
	

	

	@Test
	public void testMainPipeline0() {
		NLPPipeline pipe = new NLPPipeline();
		Transcription t = new Transcription
		("I wish I could get a set of those things that I have wanted for a while. i need something which will be able to adapt to my voice as well as my wifes. " +
				"deciding which is the nicest shirt is really challenging. we would very much like to get our floor resurfaced. Its amazing what you can buy these days. " +
				"producing a list of products and brands is quite hard. why do humans have to speak in such complicated ways. under what circumstances is it acceptable to punch your developers. a lot of people ask me about my expensive cars and jeWEllery"
				,new Date());
		pipe.processText(t);
	}
	
	@Test
	public void testMainPipeline1() {
		NLPPipeline pipe = new NLPPipeline();
		Transcription t = new Transcription
		("three brands does axe and Old Spice have generated tremendous consumer interests and identification in a historical low involvement category when you would never expect to get attention on social media",new Date());
		pipe.processText(t);
	}
	
	@Test
	public void testMainPipeline2() {
		NLPPipeline pipe = new NLPPipeline();
		Transcription t = new Transcription
		("the idea that consumers could possibly want to talk about Corona or cause in the same way that they debate the Talents of Ronaldo and Messi is silly",new Date());
		pipe.processText(t);
	}
	
	@Test
	public void testMainPipeline3a() {
		NLPPipeline pipe = new NLPPipeline();
		Transcription t = new Transcription
		("I really love car manufacturers Audi as well as BMW and Alfa Romeo",new Date());
		pipe.processText(t);
	}
	
	@Test
	public void testMainPipeline3b() {
		NLPPipeline pipe = new NLPPipeline();
		Transcription t = new Transcription
		("Car manufacturers Audi as well as BMW and Alfa Romeo are amazing, i love them",new Date());
		pipe.processText(t);
	}
	
	@Test
	public void testMainPipeline4a() {
		NLPPipeline pipe = new NLPPipeline();
		Transcription t = new Transcription
		("what is hard is that Gucci and Louis Vuitton a tricky names to capture",new Date());
		pipe.processText(t);
	}
	
	
	@Test
	public void testMainPipeline4b() {
		NLPPipeline pipe = new NLPPipeline();
		Transcription t = new Transcription
		("I am most fond of the handbags produced by Louis Vuitton",new Date());
		pipe.processText(t);
	}
	
	@Test
	public void testMainPipeline5() {
		NLPPipeline pipe = new NLPPipeline();
		Transcription t = new Transcription
		("I do most of my food shopping at Marks and Spencer these days but in the US Walmart is a huge retailer",new Date());
		pipe.processText(t);
	}
	
	@Test
	public void testMainPipeline6() {
		NLPPipeline pipe = new NLPPipeline();
		Transcription t = new Transcription
		("I'm not sure about Starbucks coffee or that from Caffe Nero but Carluccio's is great",new Date());
		pipe.processText(t);
	}

	@Test
	public void testMainPipeline7() {
		NLPPipeline pipe = new NLPPipeline();
		Transcription t = new Transcription
		("I'm not sure about Starbucks coffee",new Date());
		pipe.processText(t);
	}
	
}
