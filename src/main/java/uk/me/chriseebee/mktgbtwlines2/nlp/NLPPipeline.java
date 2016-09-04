package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.watson.developer_cloud.alchemy.v1.model.Sentiment;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;
import uk.me.chriseebee.mktgbtwlines2.nlp.ibm.AlchemyClient;

public class NLPPipeline {

	Logger logger = LoggerFactory.getLogger(NLPPipeline.class);
	Properties props = null;
	StanfordCoreNLP pipeline = null;
	AlchemyClient ac = null;
	NamedEntityManager nem = null;
	
	public NLPPipeline() {
		props = new Properties();
		//props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
		pipeline = new StanfordCoreNLP(props);
		ac = new AlchemyClient();
		nem = new NamedEntityManager();
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
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		List<String> consecutiveNouns = new ArrayList<String>();
		String lastNounType = null;
		//String nounType = null;
		InterestingEvent ev = null;
		
		for(CoreMap sentence: sentences) {
			System.out.println(sentence);
		
			// This is the sentiment from Stanford
			// String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
			
			Sentiment sentiment = null;
			
			List<CoreLabel> words = sentence.get(TokensAnnotation.class);
			
			//Find intent words in the sentence and return as comma separated
			String intents = nem.getIntents(words);
			
		  // traversing the words in the current sentence
		  // a CoreLabel is a CoreMap with additional token-specific methods
		  for (CoreLabel token: words) {
		    // this is the text of the token
		    String word = token.get(TextAnnotation.class);
		    // this is the POS tag of the token
		    String pos = token.get(PartOfSpeechAnnotation.class);
		    // this is the NER label of the token
		   // String ne = token.get(NamedEntityTagAnnotation.class);
		    
		    // init the interesting event
		    if (ev==null) { 
	    		ev = new InterestingEvent(); 
	    		ev.setIntent(intents);
	    		ev.setDateTime(transcription.getAudioStartDate().getTime());
	    	}
		    
		    logger.debug("Word="+word+":"+pos);
		    
		    if (pos.startsWith("NN")) {
		    	
		    	// So it's possible that this word is a single product or 
		    	// brand, 
		    	// It could also be part of a multiple word product/brand 
		    	
		    	// Also it is critical that a brand name before or after a product are recognized separately
		    	String res = nem.isWordRecognized(word,null);
		    	
		    	if (res!=null) {

		    		if (lastNounType!=null && !res.equals(lastNounType)) {
		    			logger.info("Process 1");
		    			processPhrase(consecutiveNouns, ev,sentiment, sentence,lastNounType);
		    		}
		    		
		    		logger.info("Word="+word+":"+pos+", was recognized as a "+res);
		    		consecutiveNouns.add(word);
		    	} else {
			    	if (consecutiveNouns.size()>0) {
			    		logger.info("Process 1b");
			    		processPhrase(consecutiveNouns, ev,sentiment, sentence,lastNounType);
			    	}
		    	}
		    	lastNounType = res;

		    } else {
		    	
		    	if (consecutiveNouns.size()>0) {
		    		logger.info("Process 2");
		    		processPhrase(consecutiveNouns, ev,sentiment, sentence,lastNounType);
		    	}
		    }
		        
		  }
		  
		  // End of loop - also check if the last words were nouns and therefore deserve processing
	    	if (consecutiveNouns.size()>0) {
	    		logger.info("Process 3");
	    		processPhrase(consecutiveNouns, ev,sentiment, sentence,lastNounType);
	    	}
	    	
		}
	}
	
	private void processPhrase(List<String> consecutiveNouns, InterestingEvent ev
			,Sentiment sentiment, CoreMap sentence, String lastNounType)  {
		
		
    	// So the word is not a noun. 
    	// Are there any in the buffer that are so that we can finalise them?

		String[] nouns = new String[consecutiveNouns.size()];
		nouns = consecutiveNouns.toArray(nouns);
		String phrase = Arrays.stream(nouns).collect(Collectors.joining(" "));
		// Now check if it exists in the brand list
		logger.info("Phrase to test ="+phrase);
		
		if (sentiment == null ) { 
			// This is the sentiment analysis from Watson which is better
			sentiment = ac.getSentenceSentiment(sentence.toString());    				
		}
		if (sentiment != null) {
			logger.debug("Sentiment = "+sentiment);
			double d = 0;
			if (sentiment.getScore()!=null) { d = sentiment.getScore(); }
			
			ev.setSentiment(d);
		}
		if (consecutiveNouns.size()==1) {
			ev.setIdentifiedEntity(consecutiveNouns.get(0));
			ev.setIdentifiedEntityType(lastNounType);
			sendInterestingEventToStorage(ev);
		} else {
			String nounType3 = nem.isPhraseRecognized(phrase,null);
			
			if (nounType3!=null) {
				logger.info("Phrase="+phrase+", has been identified as "+nounType3);
				ev.setIdentifiedEntity(phrase);
				ev.setIdentifiedEntityType(nounType3);
					
				sendInterestingEventToStorage(ev);
				consecutiveNouns.clear();
				sentiment=null;
			} else {
				// each individual noun merits an event
				for (int i=0;i<nouns.length;i++) {
					ev.setIdentifiedEntity(nouns[i]);
					ev.setIdentifiedEntityType(lastNounType);
					sendInterestingEventToStorage(ev);
				}
				consecutiveNouns.clear();
				sentiment=null;
			}
		}
		consecutiveNouns.clear();
		sentiment=null;
	}
	
	private void sendInterestingEventToStorage(InterestingEvent ev) {
		ThreadCommsManager.getInstance().getInfluxMessageQueue().add(ev);
		logger.info(ev.toString());
	}
}
