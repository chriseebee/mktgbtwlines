package uk.me.chriseebee.mktgbtwlines.speech2text.ibm;

import java.io.ByteArrayInputStream;
import java.io.File;

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

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.http.ServiceCall;
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
	
	public WatsonClient(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	

	
	private void setup() {
		
	}
	
	public void processBuffers() {
		 SpeechToText service = new SpeechToText();
		 service.setUsernameAndPassword(username, password);
		    	
		    ServiceCall<SpeechSession> session = service.createSession(SpeechModel.EN_UK_BROADBANDMODEL);

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
		    boolean keepGoing = true;
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
	
}

