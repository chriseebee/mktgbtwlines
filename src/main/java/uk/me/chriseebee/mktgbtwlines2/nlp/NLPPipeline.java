package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

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
	Properties props = null;
	StanfordCoreNLP pipeline = null;
	
	public NLPPipeline() {
		props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");
		pipeline = new StanfordCoreNLP(props);
	}
	/**
	 * 
	 * @param chunk
	 * @param dateTimeMsAsString - this is the time the recording was made in Milliseconds since epoch
	 */
	public void processText (Transcription transcription) {


		// create an empty Annotation just with the given text
		Annotation document = new Annotation(transcription.getTranscriptionText());

		// run all Annotators on this text
		pipeline.annotate(document);
		
		NamedEntityManager nem = new NamedEntityManager();
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		List<String> consecutiveNouns = new ArrayList<String>();
		String nounType = null;
		InterestingEvent ev = null;
		
		for(CoreMap sentence: sentences) {
			System.out.println(sentence);
		
			String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
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
	    		ev.setDateTime(transcription.getAudioStartDate().getTime());
	    	}
		    
		    if (pos.startsWith("NN")) {
		    	
		    	// So it's possible that this word is a single product or 
		    	// brand, 
		    	// It could also be part of a multiple word product/brand 
		    	String res = nem.isPhraseRecognized(word,null);
		    	if (res!=null) {
		    		logger.info("Word="+word+":"+pos+", was recognized as a "+res);
		    		consecutiveNouns.add(word);
		    	}

		    } else {
		    	
		    	if (consecutiveNouns.size()>0) {
			    	// So the word is not a noun. 
			    	// Are there any in the buffer that are so that we can finalise them?
	
	    			String[] nouns = new String[consecutiveNouns.size()];
	    			nouns = consecutiveNouns.toArray(nouns);
	    			String phrase = Arrays.stream(nouns).collect(Collectors.joining(" "));
	    			// Now check if it exists in the brand list
	    			logger.info("Phrase to test ="+phrase);
	    			
	    			String nounType3 = nem.isPhraseRecognized(phrase,null);
	    			
	    			if (nounType3!=null) {
	    				logger.info("Phrase="+phrase+", has been identified as "+nounType3);
		    			ev.setIdentifiedEntity(phrase);
		    			ev.setIdentifiedEntityType(nounType3);
		    			ev.setSentiment(sentiment);
		    			sendInterestingEventToStorage(ev);
		    			consecutiveNouns.clear();
	    			}
		    	}

		    	

		    	//TODO	: Use better API to get this	    	
//		    	if (pos.startsWith("JJ")) {
//		    		ev.setAdjective(word);
//		    	}

		    	//TODO	: Use better API to get this		    	
//		    	if (pos.startsWith("VB")) {
//		    		ev.setIntent(word);
//		    	}
		    }
	    
		    
		    
	          
	          
		  }
		}
	}
	
	private void sendInterestingEventToStorage(InterestingEvent ev) {
		ThreadCommsManager.getInstance().getInfluxMessageQueue().add(ev);
		logger.info(ev.toString());
	}
}
