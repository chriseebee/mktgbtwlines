package uk.me.chriseebee.mktgbtwlines2;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechModel;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

public class WatsonRecognizerMic {

	  
	  /**
	   * The main method.
	   *
	   * @param args the arguments
	   * @throws Exception the exception
	   */
	  public static void main(final String[] args) throws Exception {
	    SpeechToText service = new SpeechToText();
	    service.setUsernameAndPassword("d0a6414d-0694-4710-ad9c-b195aa88e7f7", "h4vad4oKV7SS");
	    	
	    ServiceCall<SpeechSession> session = service.createSession(SpeechModel.EN_UK_BROADBANDMODEL);

	    // Signed PCM AudioFormat with 16kHz, 16 bit sample size, mono
	    int sampleRate = 16000;
	    AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
	    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

	    if (!AudioSystem.isLineSupported(info)) {
	      System.out.println("Line not supported");
	      System.exit(0);
	    }

	    TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
	    line.open(format);
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
    		

    		System.out.println("Loop = "+listenLoop++);
    		
	    	service.recognizeUsingWebSocket(audio, options, new BaseRecognizeCallback() {
		      @Override
		      public void onTranscription(SpeechResults speechResults) {
		    	  for (Transcript t : speechResults.getResults()) {
		    			  System.out.println(t.isFinal() + ":" + t.getAlternatives().get(0));
		    	  }
		      }
		      @Override
		      public void onDisconnected() {
		        System.out.println("Disconnected");
		      }
		    });

		    System.out.println("Listening to your voice for the next 10s...");
		    Thread.sleep(10 * 1000);
    	}
    	
	    // closing the WebSockets underlying InputStream will close the WebSocket itself.
	    line.stop();
	    line.close();

	    System.out.println("Fin.");
	  }
	}
