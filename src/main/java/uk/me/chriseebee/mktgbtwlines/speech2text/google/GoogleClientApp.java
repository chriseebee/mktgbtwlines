package uk.me.chriseebee.mktgbtwlines.speech2text.google;

import io.grpc.ManagedChannel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.audio.TimedAudioBuffer;
import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;
import uk.me.chriseebee.mktgbtwlines2.nlp.Transcription;

import com.examples.cloud.speech.AsyncRecognizeClient;
import com.google.cloud.speech.v1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1beta1.StreamingRecognitionResult;
import com.google.cloud.speech.v1beta1.StreamingRecognizeResponse;

public class GoogleClientApp {

	Logger logger = LoggerFactory.getLogger(GoogleClientApp.class);
	
	//String audioFile = "";
    String host = "speech.googleapis.com";
    Integer port = 443;
    Integer sampling = 16000;
	
    private TimedAudioBuffer tab =null;
    ManagedChannel channel = null;
    StreamingRecognizeClient2 client = null;
    
	public GoogleClientApp() {  
		try {
			channel = AsyncRecognizeClient.createChannel(host, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void processFile(File f) {
        InputStream inputStream = null;

	    if (f!=null) {
		  try {
			inputStream =  new FileInputStream(f);
		  } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
	    }
  
	    logger.info("Starting to process new File");
		try {
			client = new StreamingRecognizeClient2(channel, sampling);
			Date startDate = new Date();
			System.out.println("Time = "+startDate.toString());
			client.setup();
			client.recognize(inputStream, new Date());
			processResponse(client.getResponses());
		} catch (IOException | InterruptedException e) {
			logger.error("Google Streaming Input Stream Recognize Error",e);
		} finally {
		  try {
				client.shutdown();
			} catch (Exception e) {
				logger.error("Google Streaming Shutdown Error",e);
			}
		}
	}
	
	
	public void processBuffer(TimedAudioBuffer buffer) {
	    
	    logger.info("Starting to process new Buffer");
		try {
			ByteArrayInputStream is = new  ByteArrayInputStream(tab.getBuffer());
			//AudioInputStream ais = new AudioInputStream(is, AudioFormat.WAV,(long)16000);
			client = new StreamingRecognizeClient2(channel, sampling);
			Date startDate = new Date();
			System.out.println("Time = "+startDate.toString());
			client.setup();
			client.recognize(is, startDate );
			processResponse(client.getResponses(),startDate);
		} catch (IOException | InterruptedException e) {
			logger.error("Google Streaming Input Stream Recognize Error",e);
		} finally {
	      try {
				client.shutdown();
			} catch (Exception e) {
				logger.error("Google Streaming Shutdown Error",e);
			}
	    }
	}
	
	/*
	 * This method looks through all the responses and gets the 'final' transcriptions
	 * which it then sends to the 
	 */
	 private void processResponse(List<StreamingRecognizeResponse> responses,Date date) {
		  
			for (StreamingRecognizeResponse r : responses) { 
			    for (StreamingRecognitionResult res: r.getResultsList()) {
			       //System.out.println("IsFinal = "+res.getIsFinal());
			       if (res.getIsFinal()) {
				       for (SpeechRecognitionAlternative sra: res.getAlternativesList()) {
				    	   logger.info("*** Transcript = "+sra.getTranscript());
				    	   ThreadCommsManager.getInstance().getNlpMessageQueue().add(new Transcription(sra.getTranscript(),date));
				       }
				       return;
			       }
			    }
			}	    
		    return;
	    }

}
