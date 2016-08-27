package uk.me.chriseebee.mktgbtwlines2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechModel;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechSession;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

/**
 * Hello world!
 *
 */
public class App {


	public App () {
System.out.println( "Hello World!" );
    	
    	SpeechToText service = new SpeechToText();
    	service.setUsernameAndPassword("d0a6414d-0694-4710-ad9c-b195aa88e7f7", "h4vad4oKV7SS");
    	
    	ServiceCall<List<SpeechModel>> models =  service.getModels();
    	
    	List<SpeechModel> lm = models.execute();
  
    	for (SpeechModel m : lm ) {
    		System.out.println("Model = "+m.getName());
    	}
    	
    	ServiceCall<SpeechModel> modelCall= service.getModel("en-UK_NarrowbandModel");
    	SpeechModel model = modelCall.execute();
    	
    	ServiceCall<SpeechSession>  session = service.createSession(model);
    	
    	InputStream audio;
        TargetDataLine line;
		try {
			
            AudioFormat format = getAudioFormat();
            
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
 
            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            
            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
            Mixer mixer = AudioSystem.getMixer(mixerInfo[0]); 
     
            AudioUtils au = new  AudioUtils();
            au.printMixerSupport (mixer);
            

            line = (TargetDataLine)mixer.getLine(info);

            line.open(format);
            line.start();   // start capturing
 
            System.out.println("Start capturing...");
 
            AudioInputStream ais = new AudioInputStream(line);
 
            System.out.println("Start recording...");
            
			audio = new FileInputStream("/Users/cbell/Desktop/temp.flac");
			
	    	RecognizeOptions options = new RecognizeOptions.Builder()
	    	  .continuous(true)
	    	  .interimResults(false)
	    	  .contentType(HttpMediaType.AUDIO_FLAC)
	    	  .build();

	    	 service.recognizeUsingWebSocket(audio, options, new BaseRecognizeCallback() {
	    		    @Override
	    		    public void onTranscription(SpeechResults speechResults) {
	    		      System.out.println(speechResults);
	    		    }
	    	 });
	    		  // wait 20 seconds for the asynchronous response
	    		  try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} catch (LineUnavailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


    		  
//    	SpeechResults transcript = service.recognize(audio, options).execute();
//    	System.out.println(transcript);
	}
    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return format;
    }
    
    public static void main( String[] args )
    {
    	App app = new App();

    }
}
