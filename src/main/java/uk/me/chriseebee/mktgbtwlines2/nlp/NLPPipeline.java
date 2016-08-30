package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;

public class NLPPipeline {

	Logger logger = LoggerFactory.getLogger(NLPPipeline.class);
	
	/**
	 * 
	 * @param chunk
	 * @param dateTimeMsAsString - this is the time the recording was made in Milliseconds since epoch
	 */
	public void processText (String chunk, Date date) {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(chunk.toLowerCase());

		// run all Annotators on this text
		pipeline.annotate(document);
		
		NamedEntityManager nem = new NamedEntityManager();
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		List<String> consecutiveNouns = new ArrayList<String>();
		String nounType = null;
		InterestingEvent ev = null;
		
		for(CoreMap sentence: sentences) {
			System.out.println(sentence);
		  // traversing the words in the current sentence
		  // a CoreLabel is a CoreMap with additional token-specific methods
		  for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
		    // this is the text of the token
		    String word = token.get(TextAnnotation.class);
		    // this is the POS tag of the token
		    String pos = token.get(PartOfSpeechAnnotation.class);
		    // this is the NER label of the token
		    String ne = token.get(NamedEntityTagAnnotation.class);
		    
		    // init the interesting event
		    if (ev==null) { 
	    		ev = new InterestingEvent(); 
	    		ev.setDateTime(date.getTime());
	    	}
		    
		    if (pos.startsWith("NN")) {
		    	System.out.println("Word="+word+":"+pos);
		    	// So it's possible that this word is a single product or 
		    	// brand, 
		    	// It could also be part of a multiple word product/brand 
		    	
		    	// First let's check that.
		    	nounType = nem.isWordRecognized(word, nounType);
		    	// If it is, then add it to a list for further checking based on further words
		    	if (nounType!=null) {
		    		consecutiveNouns.add(word);
		    	}
		    } else {
		    	// So the word is not a noun. 
		    	// Are there any in the buffer that are so that we can finalise them?
		    	if (consecutiveNouns.size()>=1) {
		    		// Yes, is this a single word or multiple
		    		if (consecutiveNouns.size()==1) { 
		    			// this is a single word
		    			ev.setIdentifiedEntity(consecutiveNouns.get(0));
		    			ev.setIdentifiedEntityType(nounType);
		    		} else {
		    			
		    		}
		    	}
		    	
		    	if (pos.startsWith("JJ")) {
		    		ev.setAdjective(word);
		    	}
		    	
		    	if (pos.startsWith("VB")) {
		    		ev.setIntent(word);
		    	}
		    }
		    
	          String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
	          ev.setSentiment(sentiment);
	          
	          sendInterestingEventToStorage(ev);
		  }
		}
	}
	
	private void sendInterestingEventToStorage(InterestingEvent ev) {
		ThreadCommsManager.getInstance().getInfluxMessageQueue().add(ev);
		logger.info(ev.toString());
	}
}
