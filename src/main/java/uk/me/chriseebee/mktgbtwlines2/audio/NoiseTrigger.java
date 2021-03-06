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
    private int THRESHOLD = 20;

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
	
	private void ambientListeningLoop() {
	    MicrophoneAnalyzer mic = new MicrophoneAnalyzer();
	   // mic.setAudioFile(new File("AudioTestNow.flac"));
	    mic.open();
	    while(running){
	       // mic.open();
	        int volume = mic.getAudioVolume();
	        boolean isSpeaking = (volume > THRESHOLD);
	        System.out.print("."+volume);
	        if(isSpeaking){
	            try {
	                System.out.println(".");
	          
	                do{
		                if (!ThreadCommsManager.getInstance().isRecording()) {
		                	logger.info("Starting the Recorder by sending message to queue");
		                	ThreadCommsManager.getInstance().getNoiseDetectionQueue().add(new Date());
		                }
	                    Thread.sleep(1000);//Updates every second
	                }
	                while(mic.getAudioVolume() > THRESHOLD);
	            } catch (Exception e) {
	                logger.error("Error Occured in Mic Threshold loop",e);
	           
	            }
	            finally{
	             //   mic.close();//Makes sure microphone closes on exit.
	            }
	        }
	        try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("Error Occured in thread sleep",e);
			}//Updates every 0.1 second
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
