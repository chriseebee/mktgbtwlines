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
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechModel;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechSession;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

public class WatsonClient {

	Logger logger = LoggerFactory.getLogger(WatsonClient.class);
	
	private String username = null;
	private String password = null;
	private String nluUsername = null;
	private String nluPassword = null;
	
	public WatsonClient() throws ConfigurationException {
		AppConfig cl = ConfigLoader.getConfig();
		username = cl.getWatsonKeys().get("username");
		password = cl.getWatsonKeys().get("password");
		nluUsername = cl.getWatsonKeys().get("nluUsername");
		nluPassword = cl.getWatsonKeys().get("nluPassword");
	}
	
	public void processBuffers() {
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

		    System.out.println("Fin.");
	}

	public void processStream() {
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

			    System.out.println("Listening to your voice for the next 10s...");
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
	public SpeechResults processFile(File f) {
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
	
	public List<InterestingEvent> mapEntities(AnalysisResults ar) {
		
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
	
	public AnalysisResults getEntities(String text) {
		
		NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
				  NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
				  nluUsername,
				  nluPassword
				);

		CategoriesOptions categories = new CategoriesOptions();
		
		EntitiesOptions entitiesOptions = new EntitiesOptions.Builder()
				  .emotion(false)
				  .sentiment(true)
				  .limit(10)
				  .build();

		Features features = new Features.Builder()
		  .categories(categories)
		  .entities(entitiesOptions)
		  .build();

		AnalyzeOptions parameters = new AnalyzeOptions.Builder()
		  .text(text)
		  .features(features)
		  .build();

		AnalysisResults response = service
		  .analyze(parameters)
		  .execute();
		
		
		System.out.println(response);
		
		return response;
	}
	
	
	
}

