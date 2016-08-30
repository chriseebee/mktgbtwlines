package uk.me.chriseebee.mktgbtwlines2.audio;

import com.darkprograms.speech.microphone.*;

public class NoiseTrigger extends Thread {

    private volatile boolean running = true;
    final int THRESHOLD = 20;

	
	private void ambientListeningLoop() {
	    MicrophoneAnalyzer mic = new MicrophoneAnalyzer();
	   // mic.setAudioFile(new File("AudioTestNow.flac"));
	    while(true){
	        mic.open();
	        
	        int volume = mic.getAudioVolume();
	        boolean isSpeaking = (volume > THRESHOLD);
	        System.out.print("."+volume);
	        if(isSpeaking){
	            try {
	                System.out.println("RECORDING...");
	                // Send message to recording thread
	                //mic.captureAudioToFile(mic.getAudioFile());//Saves audio to file.
	                do{
	                	System.out.println(" Looping in recording mode. Volume = "+mic.getAudioVolume());
	                    Thread.sleep(1000);//Updates every second
	                }
	                while(mic.getAudioVolume() > THRESHOLD);
	                System.out.println("Looping back");//Restarts loops
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	                System.out.println("Error Occured");
	            }
	            finally{
	                mic.close();//Makes sure microphone closes on exit.
	            }
	        }
	        try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//Updates every 0.1 second
	    }
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
