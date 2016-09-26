package uk.me.chriseebee.mktgbtwlines2.audio;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.mappers.AppConfig;

import com.darkprograms.speech.microphone.MicrophoneAnalyzer;


public class NoiseTrigger extends Thread {

	Logger logger = LoggerFactory.getLogger(NoiseTrigger.class);
	
    private volatile boolean running = true;
    private int THRESHOLD = 30;
    
    private double silenceCounter = 0;
    private boolean isSpeaking = false;
    private long speakingStarted = 0;
    
    private static final long chunkTimeLength = 250; //ms
    // constants for the calculation of probability
    private static final double midSentenceLength = 600; //ms
    private static final double midUnpredictablePause = 750; //ms		

    public NoiseTrigger() {
		AppConfig ac = null; 
		try {
			ConfigLoader cl = ConfigLoader.getConfigLoader();
			ac = cl.getConfig();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("BIG ERROR LOADING CONFIG",e);
		}
		
		THRESHOLD = new Integer(ac.getAudioOptions().get("triggerThreshold")).intValue();
    	
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
        	if (isSentenceTermination()  && isSpeaking) {
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
    	//ThreadCommsManager.getInstance().getNoiseDetectionQueue().add(tab);
    }
    
	private void ambientListeningLoop() {
	    MicrophoneAnalyzer mic = new MicrophoneAnalyzer();

	    mic.open();
	    while(running){
	       // mic.open();
	        int volume = mic.getAudioVolume();
	        //int ff = mic.getFrequency();
	        manageState(volume);

	        try {
				Thread.sleep(chunkTimeLength);
			} catch (InterruptedException e) {
				logger.error("Error Occured in thread sleep",e);
			}
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
    
	
	
//    private final static MicrophoneAnalyzer microphone = new MicrophoneAnalyzer(FLACFileWriter.FLAC);
//
//
//
//    public static void ambientListening(){
//        String filename = "wav.test";
//        try{
//            microphone.captureAudioToFile(filename);
//        }
//        catch(Exception ex){
//            ex.printStackTrace();
//            return;
//        }
//        final int SILENT = microphone.getAudioVolume();
//        boolean hasSpoken = false;
//        boolean[] speaking = new boolean[10];
//        Arrays.fill(speaking, false);
//        for(int i = 0; i<100; i++){
//            for(int x = speaking.length-1; x>1; x--){
//                speaking[x] = speaking[x-1];
//            }
//            int frequency = microphone.getFrequency();
//            int volume = microphone.getAudioVolume();
//            speaking[0] = frequency<255 && volume>SILENT && frequency>85;
//            System.out.println(speaking[0]);
//            boolean totalValue = false;
//            for(boolean bool: speaking){
//                totalValue = totalValue || bool;
//            }
//            //if(speaking[0] && speaking[2] && speaking[3] && microphone.getAudioVolume()>10){
//            if(totalValue && microphone.getAudioVolume()>20){   
//                hasSpoken = true;
//            }
//            if(hasSpoken && !totalValue){
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                break;
//            }
//        }
//        if(hasSpoken){
//        Recognizer rec = new Recognizer(Recognizer.Languages.ENGLISH_US);
//        GoogleResponse out = rec.getRecognizedDataForWave(filename);
//        }
//        ambientListening();
//    }
}
