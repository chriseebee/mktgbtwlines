package uk.me.chriseebee.mktgbtwlines2.audio;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.mappers.AppConfig;
import uk.me.chriseebee.mktgbtwlines2.storage.AudioClipStore;

import com.darkprograms.speech.microphone.MicrophoneAnalyzer;


public class NoiseTrigger extends Thread {

	Logger logger = LoggerFactory.getLogger(NoiseTrigger.class);
	
    private volatile boolean running = true;
    private int THRESHOLD = 49;
    
    private double silenceCounter = 0;
    private boolean isSpeaking = false;
    private long speakingStarted = 0;
    
    private static final long chunkTimeLength = 250; //ms
    // constants for the calculation of probability
    private static final double midSentenceLength = 600; //ms
    private static final double midUnpredictablePause = 750; //ms		

    private MicrophoneAnalyzer mic;
    
    // record duration, in milliseconds
    //private static final long RECORD_TIME = 200000;  // 10 seconds 
    private static final int BYTES_PER_BUFFER = 16000; //buffer size in bytes
    
    // For LINEAR16 at 16000 Hz sample rate, 16000 bytes corresponds to 0.5 seconds of audio.
    byte[] buffer = new byte[BYTES_PER_BUFFER];
    
    AudioUtils au;
    
    public NoiseTrigger(MicrophoneAnalyzer  mic) {
    	
    	this.mic = mic;
		AppConfig ac = null; 
		try {
			ac = ConfigLoader.getConfig();
			
			THRESHOLD = new Integer(ac.getAudioOptions().get("triggerThreshold")).intValue();
			
	    	au = new AudioUtils();
	    	au.setupRecording();
	    	
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("BIG ERROR LOADING CONFIG",e);
		}	
    }
    
    /**
     * Use this to override the threshold in the configuration, esp whilst testing
     * with audio files as the threshold seems to be a lot higher, more like 40
     * 
     * @param threshold
     */
    public void setThreshold(int threshold) {
    	THRESHOLD = threshold;
    }
    
    private void manageState(int volume) {
        if (volume > THRESHOLD) {
        	if (!isSpeaking) {
        		logger.info("Speaking Started");
        		speakingStarted = System.currentTimeMillis();
        	}
        	isSpeaking = true;
        	
        	// how long have we been silent for?
        	// TODO: Build this back into the model
        	//if (silenceCounter>0) {
        	//	logger.info("Silence was "+silenceCounter/(1000/chunkTimeLength)+ " seconds");
        	//}
        	// its noisy, so reset counter
        	silenceCounter = 0;
        	
        } else {
        	silenceCounter++;
        	
        	// 1 second is classed as a break in sentence or more, so stop now
        	if (isSpeaking && silenceCounter*chunkTimeLength > 500 && isSentenceTermination()) {
        		logger.info("Time to process a block of speech now");
    			// now is the time to stop the block and send the timings
    			communicateSpeechBlock ();
    			speakingStarted = 0;
        		isSpeaking = false;
        	}
        	
        	// keep SilenceCounter going, we need to track
        	// the length of silence
        }
    }
    
    private boolean isSentenceTermination() {
    	
    	// any pause > 1 second is a sentence stop
    	if (silenceCounter*chunkTimeLength > 1000) { return true; }
    	
    	// if this is negative, then it's a very short sentence, positive = longer
    	double sentenceLengthRange = (System.currentTimeMillis()-speakingStarted) - midSentenceLength ;
    	// if this is negative, then it's a very short pause, positive = longer
    	double pauseLengthRange = (silenceCounter*chunkTimeLength) - midUnpredictablePause ;
    	
    	if (sentenceLengthRange*pauseLengthRange>0) { 
    		logger.info("Sentence Termination Detected");
    		return true; 
    	} else { 
    		//logger.info("Not a Sentence Termination");
    		return false; 
    	}	
        
    }
	
    private void communicateSpeechBlock () {
    	TimedAudioBuffer tab = new TimedAudioBuffer(speakingStarted);
    	tab.setEndDateTime(System.currentTimeMillis());
    	logger.info("Block of speech identified is "+tab.getLength()/1000+" seconds long");
    	ThreadCommsManager.getInstance().getSentenceInSpeechDetectionQueue().add(tab);
    	try {
			AudioClipStore.getInstance(AudioClipStore.STORE_TYPE_DATABASE).insertBlock(tab);
		} catch (Exception e) {
			logger.error("Error inserting audio block",e);
		}
    }
    
	private void ambientListeningLoop() {

	    mic.open();
	    while(running){
	    	
	        int volume = mic.getAudioVolume(250);
	        //logger.info("."+volume);
	        //int ff = mic.getFrequency();
	        manageState(volume);

	        mic.sleepThread(250);
	    }
	    mic.close();
	}

	

    public void run() {
    	this.running = true;
    	this.ambientListeningLoop();
    }
    
    public void stopRunning() {
    	this.running = false;
    }
    
	
}
