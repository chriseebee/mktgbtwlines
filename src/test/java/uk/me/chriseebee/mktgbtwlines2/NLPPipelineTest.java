package uk.me.chriseebee.mktgbtwlines2;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import uk.me.chriseebee.mktgbtwlines2.nlp.NLPPipeline;

public class NLPPipelineTest {


	
	@Test
	public void testMainPipeline() {
		NLPPipeline pipe = new NLPPipeline();
		pipe.processText("I wish I could get a set of those things that I have wanted for a while. i need something which will be able to adapt to my voice as well as my wifes. " +
				"deciding which is the nicest shirt is really challenging. we would very much like to get our floor resurfaced. Its amazing what you can buy these days. " +
				"producing a list of products and brands is quite hard. why do humans have to speak in such complicated ways. under what circumstances is it acceptable to punch your developers. a lot of people ask me about my expensive cars and jeWEllery"
				,new Date());
		fail("Not yet implemented");
	}

}
