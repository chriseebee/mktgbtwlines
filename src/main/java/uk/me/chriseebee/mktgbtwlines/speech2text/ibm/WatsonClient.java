package uk.me.chriseebee.mktgbtwlines.speech2text.ibm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.audio.AudioUtils;
import uk.me.chriseebee.mktgbtwlines2.audio.TimedAudioBuffer;
import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;
import uk.me.chriseebee.mktgbtwlines2.config.mappers.AppConfig;
import uk.me.chriseebee.mktgbtwlines2.nlp.InfoExtractor;
import uk.me.chriseebee.mktgbtwlines2.nlp.InterestingEvent;
import uk.me.chriseebee.mktgbtwlines2.nlp.Transcription;
import uk.me.chriseebee.mktgbtwlines2.nlp.entity.Entity;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.RelationArgument;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.RelationEntity;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.RelationsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.RelationsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SemanticRolesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SemanticRolesResult;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechModel;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechSession;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

public class WatsonClient {

	static Logger logger = LoggerFactory.getLogger(WatsonClient.class);
	
	private static String username = null;
	private static String password = null;
	private static String nluUsername = null;
	private static String nluPassword = null;
	
	public static void setup() throws ConfigurationException {
		if (username==null) {
			AppConfig cl = ConfigLoader.getConfig();
			username = cl.getWatsonKeys().get("username");
			password = cl.getWatsonKeys().get("password");
			nluUsername = cl.getWatsonKeys().get("nluUsername");
			nluPassword = cl.getWatsonKeys().get("nluPassword");
		}
	}
	
	public static void processBuffers() {
		 SpeechToText service = new SpeechToText();
		 service.setUsernameAndPassword(username, password);
		    	
		//    ServiceCall<SpeechSession> session = service.createSession(SpeechModel.EN_UK_BROADBANDMODEL);

		    // Signed PCM AudioFormat with 16kHz, 16 bit sample size, mono
		    int sampleRate = 16000;
		    RecognizeOptions options = new RecognizeOptions.Builder()
		      .continuous(true)
		      .interimResults(true)
		      .timestamps(false)
		      .wordConfidence(false)
		      //.inactivityTimeout(5) // use this to stop listening when the speaker pauses, i.e. for 5s
		      .contentType(HttpMediaType.AUDIO_RAW + "; rate=" + sampleRate)
		      .build();

		    int listenLoop = 0;
		    TimedAudioBuffer tab = null;
		    
	    	while(listenLoop < 20) {
	
	    		tab = ThreadCommsManager.getInstance().getAudioBufferQueue().poll();
	    		
	    		AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(tab.getBuffer()), AudioUtils.getAudioFormat(), 160000);
	    		
		    	service.recognizeUsingWebSocket(ais, options, new BaseRecognizeCallback() {
			      @Override
			      public void onTranscription(SpeechResults speechResults) {
			    	  for (Transcript t : speechResults.getResults()) {
			    			  logger.info(t.isFinal() + ":" + t.getAlternatives().get(0));
			    	  }
			      }
			      @Override
			      public void onDisconnected() {
			    	  logger.info("Disconnected");
			      }
			    });

		    	logger.info("Listening to your voice for the next 10s...");
			    try {
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					logger.error("Thread sleep interrupted",e);
				}
			    
			    listenLoop++;
	    	}

		    logger.info("Fin.");
	}

	public static void processStream() {
		 SpeechToText service = new SpeechToText();
		 service.setUsernameAndPassword(username, password);
		    
		    //TODO: implement session pinning for Watson Calls
		    ServiceCall<SpeechSession> session = service.createSession(SpeechModel.EN_UK_BROADBANDMODEL);

		    // Signed PCM AudioFormat with 16kHz, 16 bit sample size, mono
		    int sampleRate = 16000;
		    AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
		    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

		    if (!AudioSystem.isLineSupported(info)) {
		    	logger.info("Line not supported");
		    	System.exit(0);
		    }

		    TargetDataLine line = null;
			try {
				line = (TargetDataLine) AudioSystem.getLine(info);
				line.open(format);
			} catch (LineUnavailableException e2) {
				// TODO Auto-generated catch block
				logger.error("Line Unavailable",e2);
			}

		    line.start();

		    AudioInputStream audio = new AudioInputStream(line);

		    RecognizeOptions options = new RecognizeOptions.Builder()
		      .continuous(true)
		      .interimResults(true)
		      .timestamps(false)
		      .wordConfidence(false)
		      //.inactivityTimeout(5) // use this to stop listening when the speaker pauses, i.e. for 5s
		      .contentType(HttpMediaType.AUDIO_RAW + "; rate=" + sampleRate)
		      .build();

		    int listenLoop = 0;
		    //boolean keepGoing = true;
	    	while(listenLoop < 20) {
	    		

	    		logger.info("Loop = "+listenLoop++);
	    		
		    	service.recognizeUsingWebSocket(audio, options, new BaseRecognizeCallback() {
			      @Override
			      public void onTranscription(SpeechResults speechResults) {
			    	  for (Transcript t : speechResults.getResults()) {
			    		  logger.info(t.isFinal() + ":" + t.getAlternatives().get(0));
			    	  }
			      }
			      @Override
			      public void onDisconnected() {
			    	  logger.info("Disconnected");
			      }
			    });

			    logger.info("Listening to your voice for the next 10s...");
			    try {
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e) {
					logger.error("Thread sleep interrupted",e);
				}
	    	}
	    	
		    // closing the WebSockets underlying InputStream will close the WebSocket itself.
		    line.stop();
		    line.close();

		    logger.info("Finished");
		  }
	
	/*
	 * Self Contained Tester for file based processing
	 */
	public static SpeechResults processFile(File f) {
	    SpeechToText service = new SpeechToText();
	    service.setUsernameAndPassword(username, password);
	    	
	    int sampleRate = 16000;

	    RecognizeOptions options = new RecognizeOptions.Builder()
	      .continuous(true)
	      .interimResults(true)
	      .timestamps(false)
	      .wordConfidence(false)
	      .model(SpeechModel.EN_UK_BROADBANDMODEL.getName())
	      //.inactivityTimeout(5) // use this to stop listening when the speaker pauses, i.e. for 5s
	      .contentType(HttpMediaType.AUDIO_WAV + "; rate=" + sampleRate)
	      .build();

	    	
	    SpeechResults results = service.recognize(f,options).execute();
	    
	    return results;
	}
	
	public static List<InterestingEvent> mapEntities(AnalysisResults ar) {
		
		Map<String,InterestingEvent> eventList = Collections.synchronizedMap(new HashMap<String,InterestingEvent>());
		
		CategoriesResult topCat = ar.getCategories().get(0);
		
		for (EntitiesResult er : ar.getEntities()) {
			Entity e = new Entity();
			e.setName(er.getText());
			e.setType(er.getType());
			e.setSource(Entity.SOURCE_WATSON);
			e.setValidity(Entity.VALIDITY_AFFIRMED);
			
			InterestingEvent ev = new InterestingEvent();
			
			ev.setEntity(e);
			ev.setSentiment(er.getSentiment().getScore());
			ev.setEntityCountInUtterance(er.getCount());
			e.addCategory(topCat.getLabel());
			
			eventList.put(er.getText(),ev);
		}
		
		List<InterestingEvent> list = new ArrayList<InterestingEvent>(eventList.values());
		return list;
		
	}
	
	public static AnalysisResults process(String text, List<String> annotationList) {
		
		logger.info("Callng Watson NLU");
		
		NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
				  NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
				  nluUsername,
				  nluPassword
				);
		
		Features.Builder builder = new Features.Builder();

		if (annotationList.contains(InfoExtractor.CATEGORY_ANNOTATION)) {
			CategoriesOptions categories = new CategoriesOptions();
			builder.categories(categories);
		}
		
		if (annotationList.contains(InfoExtractor.CONCEPT_ANNOTATION)) {
			ConceptsOptions.Builder cb = new ConceptsOptions.Builder();
			cb.limit(new Integer(10));
			ConceptsOptions conceptOptions = cb.build();
			builder.concepts(conceptOptions);
		}
		
		if (annotationList.contains(InfoExtractor.KEYWORD_ANNOTATION)) {
			KeywordsOptions.Builder b = new KeywordsOptions.Builder();
			b.limit(new Integer(10));
			KeywordsOptions keywordoptions = b.build();
			builder.keywords(keywordoptions);
		}
		
		if (annotationList.contains(InfoExtractor.ENTITY_ANNOTATION)) {
		
			EntitiesOptions entitiesOptions = new EntitiesOptions.Builder()
					  .emotion(false)
					  .sentiment(true)
					  .limit(10)
					  .build();
			builder.entities(entitiesOptions);
		}

		if (annotationList.contains(InfoExtractor.RELATION_ANNOTATION)) {
			RelationsOptions.Builder rb= new RelationsOptions.Builder();
			RelationsOptions relationsOptions = rb.build();
			builder.relations(relationsOptions);
			
			SemanticRolesOptions.Builder srob = new SemanticRolesOptions.Builder();
			SemanticRolesOptions sro = srob.build();
			builder.semanticRoles(sro);
			
		}

		Features features = builder.build();

		AnalyzeOptions parameters = new AnalyzeOptions.Builder()
		  .text(text)
		  .features(features)
		  .build();

		AnalysisResults response = service
		  .analyze(parameters)
		  .execute();
	
		
		logger.info("Calling Watson NLU: FINISHED");
		
		return response;
	}
	
	public static void printConfidentResults(AnalysisResults ar) {
		
	    logger.info("START +++++++++++++++++++++");
	    logger.info(ar.getAnalyzedText());
	      
		List<ConceptsResult> concepts = ar.getConcepts();
	
		if (concepts!=null) {
			for (ConceptsResult cr : concepts) {
				if (cr.getRelevance()>0.01) {
					logger.info("CONCEPT: "+cr.getText() + "/" + cr.getRelevance());
				}
			}
		}
		
		List<KeywordsResult> keywords = ar.getKeywords();
		
		if (keywords!=null) {
			for (KeywordsResult kr : keywords) {
				if (kr.getRelevance()>0.01) {
					logger.info("KEYWORD: "+kr.getText() + "/" + kr.getRelevance());
				}
			}
		}
		
		List<CategoriesResult> categories = ar.getCategories();
		
		if (categories!=null) {
			for (CategoriesResult catr : categories) {
				if (catr.getScore()>0.01) {
					logger.info("CATEGORY: "+catr.getLabel() + "/" + catr.getScore());
				}
			}
		}
		
		List<EntitiesResult> entities = ar.getEntities();
		
		if (entities!=null) {
			for (EntitiesResult e : entities) {
				if (e.getRelevance()>0.01) {
					logger.info("ENTITY: "+e.getText() + "/" + e.getType() + "/" + e.getRelevance());
				}
			}
		}
		List<RelationsResult> relations = ar.getRelations();
		
		if (relations!=null) {
			for (RelationsResult e : relations) {
				if (e.getScore()>0.01) {
					logger.info("RELATION: "+e.getType() + ". Confidence = "+e.getScore());
					for (RelationArgument ra : e.getArguments()) {
						RelationEntity re = ra.getEntities().get(0);
						logger.info(" - " + ra.getText() + "\t" + re.getText() + "\t" + re.getType());
						if (ra.getEntities().size()>1) {
							logger.warn("There are more entities to get!!");
						}
					}
				}
			}
		}
		
		List<SemanticRolesResult> sem = ar.getSemanticRoles();
		
		if (sem!=null) {
			for (SemanticRolesResult sr : sem) {
					logger.info("SEMANTIC ROLE: " + sr.getAction().getText() + " (Verb = "+sr.getAction().getVerb().getText()+"/Tense = "+sr.getAction().getVerb().getTense()+ ")");
					logger.info("  SUBJECT: " + sr.getSubject().getText());
					if (sr.getObject()!=null) {
						logger.info("  OBJECT " +  sr.getObject().getText());
					}
			}
		}
		
	    logger.info("END +++++++++++++++++++++");
		
	}
	
	
}

