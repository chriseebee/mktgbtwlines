package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.watson.developer_cloud.alchemy.v1.model.Sentiment;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import uk.me.chriseebee.mktgbtwlines.speech2text.ibm.WatsonClient;
import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;
import uk.me.chriseebee.mktgbtwlines2.nlp.entity.Entity;
import uk.me.chriseebee.mktgbtwlines2.nlp.ibm.AlchemyClient;

public class NLPPipeline {

	Logger logger = LoggerFactory.getLogger(NLPPipeline.class);
	Properties props = null;
	StanfordCoreNLP pipeline = null;
	WatsonClient wc = null;
	NamedEntityManager nem = null;

	public NLPPipeline() {
		try {
			logger.info("Setting up 1");
			props = new Properties();
			// props.setProperty("annotators", "tokenize, ssplit, pos, lemma,
			// ner, parse, dcoref, sentiment");
			logger.info("Setting up 2");
			props.setProperty("annotators", "tokenize, ssplit, pos, parse, depparse");
			logger.info("Setting up 3");
			pipeline = new StanfordCoreNLP(props);
			logger.info("Setting up 4");
			wc = new WatsonClient();
			logger.info("Setting up 5");
			nem = new NamedEntityManager();
		} catch (ConfigurationException e) {
			logger.error("Cannot setup NLPPipeline", e);
		}

	}

	/**
	 * 
	 * @param chunk
	 * @param dateTimeMsAsString
	 *            - this is the time the recording was made in Milliseconds
	 *            since epoch
	 */
	
	public List<CoreMap> getSentences(Transcription transcription) {
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(transcription.getTranscriptionText());

		// run all Annotators on this text
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		return sentences;
	}
	
	public void processText(Transcription transcription) {
		
		// String nounType = null;
		InterestingEvent ev = null;

		for (CoreMap sentence : getSentences(transcription)) {
			
			logger.debug(sentence.toString());
			
			AnalysisResults ar = wc.getEntities(sentence.toString());
			List<InterestingEvent> ieList = wc.mapEntities(ar);
			
			String sentenceCategory = ieList.get(0).getEntity().getCategory();

			// This is the sentiment from Stanford
			String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
			
			List<CoreLabel> words = sentence.get(TokensAnnotation.class);
			
			removeKnownEntitiesFromSentence(sentence.toString(),words, ieList);

			// Find intent words in the sentence and return as comma separated
			String intents = nem.getIntents(words);

			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			List<String> consecutiveNouns = new ArrayList<String>();
			List<String> allNounsInSentence = new ArrayList<String>();
			String lastNounType = null;
			
			for (CoreLabel token : words) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);


//				// init the interesting event
//				if (ev == null) {
//					ev = new InterestingEvent();
//					ev.setIntent(intents);
//					ev.setDateTime(transcription.getAudioStartDate().getTime());
//				}

				logger.debug("Word=" + word + ":" + pos);

				if (pos.startsWith("NN")) {

					// So it's possible that this word is a single product or
					// brand,
					// It could also be part of a multiple word product/brand

					// Also it is critical that a brand name before or after a
					// product are recognized separately
					String res = nem.isWordRecognized(word, null);

					if (res != null) {

						if (lastNounType != null && !res.equals(lastNounType)) {
							logger.debug("Process 1");
							allNounsInSentence.add(joinNounsToPhrase(consecutiveNouns));
							//processPhrase(consecutiveNouns, ev, sentiment, sentence, lastNounType);
						}

						logger.info("Word=" + word + ":" + pos + ", was recognized as a " + res);
						consecutiveNouns.add(word);
					} else {
						if (consecutiveNouns.size() > 0) {
							logger.debug("Process 1b");
							//processPhrase(consecutiveNouns, ev, sentiment, sentence, lastNounType);
						}
					}
					lastNounType = res;

				} else {

					if (consecutiveNouns.size() > 0) {
						logger.debug("Process 2");
						//processPhrase(consecutiveNouns, ev, sentiment, sentence, lastNounType);
					}
				}

			}

			// End of loop - also check if the last words were nouns and
			// therefore deserve processing
			if (consecutiveNouns.size() > 0) {
				logger.debug("Process 3");
				//processPhrase(consecutiveNouns, ev, sentiment, sentence, lastNounType);
			}

		}
	}
	

	void removeKnownEntitiesFromSentence(String sentence, List<CoreLabel> words, List<InterestingEvent> ieList) {
		
		List<String> strings = words.stream()
				   .map(object -> object.originalText().toLowerCase())
				   .collect(Collectors.toList());
		
		for (InterestingEvent ie : ieList) {
			String entityName = ie.getEntity().getName().toLowerCase();
			String[] splitNameArray = entityName.split(" ");
			int entityWordCount = ie.getEntityCountInUtterance();
			
			for (int i=0;i<entityWordCount;i++) {
				
				if (splitNameArray.length>1) {
					boolean isMultiWordEntityAMatch = true;
					logger.info("Looking for \""+entityName+"\" in words");
					logger.info("Trying to split the sentence on this word: "+splitNameArray[0]);
					int indexOfFirstWord = strings.indexOf(splitNameArray[0]);
					logger.info("Index = "+indexOfFirstWord);
					for (int k=1;k<splitNameArray.length-1;k++) {
						if (!strings.get(indexOfFirstWord+k-1).equals(splitNameArray[indexOfFirstWord+k-1])) {
							isMultiWordEntityAMatch = false;
						}
					}
					
					if (isMultiWordEntityAMatch) {
						for (int l=indexOfFirstWord;l<indexOfFirstWord+splitNameArray.length;l++) {
							strings.remove(l);
							words.remove(l);
						}
					}
				} else {
					int i1 = strings.indexOf(entityName.toLowerCase());
					logger.info("Index of Word : "+entityName.toLowerCase()+" is: "+i1+". Strings length="+strings.size()+" and Words length="+words.size());
					words.remove(i1);
					strings.remove(i1);
					logger.info("Index of Word : "+entityName.toLowerCase()+" is: "+i1+". Strings length="+strings.size()+" and Words length="+words.size());
					
				}
			}
		
		}
		
	}
	
	private String joinNounsToPhrase(List<String> consecutiveNouns ) {
		String[] nouns = new String[consecutiveNouns.size()];
		nouns = consecutiveNouns.toArray(nouns);
		String phrase = Arrays.stream(nouns).collect(Collectors.joining(" "));
		return phrase;
	}

	private void processPhrase(List<String> consecutiveNouns, InterestingEvent ev, Sentiment sentiment,
			CoreMap sentence, String lastNounType) {

		logger.info("Phrase to test =" + sentence.toString());

		if (consecutiveNouns.size() == 1) {
			Entity e = new Entity();
			e.setName(consecutiveNouns.get(0));
			e.setType(lastNounType);
			ev.setEntity(e);
			sendInterestingEventToStorage(ev);
		} else {
			String nounType3 = nem.isPhraseRecognized(sentence.toString(), null);

			if (nounType3 != null) {
				logger.info("Phrase=" + sentence.toString() + ", has been identified as " + nounType3);
				Entity e = new Entity();
				e.setName(sentence.toString());
				e.setType(nounType3);
				ev.setEntity(e);
				sendInterestingEventToStorage(ev);
				consecutiveNouns.clear();
				sentiment = null;
			} else {
				// each individual noun merits an event
				for (int i = 0; i < consecutiveNouns.size(); i++) {
					Entity e = new Entity();
					e.setName(consecutiveNouns.get(i));
					e.setType(lastNounType);
					ev.setEntity(e);
					sendInterestingEventToStorage(ev);
				}
				consecutiveNouns.clear();
				sentiment = null;
			}
		}
		consecutiveNouns.clear();
		sentiment = null;
	}

	private void sendInterestingEventToStorage(InterestingEvent ev) {
		ThreadCommsManager.getInstance().getStorageMessageQueue().add(ev);
		logger.info(ev.toString());
	}
}
