package uk.me.chriseebee.mktgbtwlines2.audio;

import javax.sound.sampled.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;

import java.io.IOException;
import java.util.Date;
 
/**
 * A sample program is to demonstrate how to record sound in Java
 * author: www.codejava.net
 * 
 * http://www.codejava.net/coding/capture-and-record-sound-into-wav-file-with-java-sound-api
 */
public class AudioRecorder extends Thread {
	
	Logger logger = LoggerFactory.getLogger(AudioRecorder.class);
	
    // record duration, in milliseconds
    //private static final long RECORD_TIME = 200000;  // 10 seconds 
    private static final int BYTES_PER_BUFFER = 320000; //buffer size in bytes

    private volatile boolean running = true;
    
    // For LINEAR16 at 16000 Hz sample rate, 320000 bytes corresponds to 10 seconds of audio.
    byte[] buffer = new byte[BYTES_PER_BUFFER];
    
    AudioUtils au;
    // format of audio file for Wave
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
    TargetDataLine line;
 
    //private DataLine.Info info =  null;
    private AudioInputStream ais = null;
    
    public AudioRecorder() {
    	
    	au = new AudioUtils();
    	au.setupRecording();
    }

    /**
     * Captures the sound and record into a WAV file
     */
    
    public void run() {
        while (running) {
        	if (ThreadCommsManager.getInstance().isRecording()) {
        		Date d = new Date();
            	TimedAudioBuffer tab = new TimedAudioBuffer(d);
            	int bytesRead = 0;
				try {
					bytesRead = ais.read(buffer);
				} catch (IOException e1) {
					logger.error("Error reading from input stream",e1);
				}
            	if (bytesRead>0) {
	            	tab.setEndDateTime(new Date());
	            	tab.setBuffer(buffer);
	            	logger.info("Putting Audio Buffer on Queue");
	            	ThreadCommsManager.getInstance().getAudioBufferQueue().add(tab);
            	} 
            	
//        		try {
//					Thread.sleep(10000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
            	ThreadCommsManager.getInstance().setRecording(false);
        	} else {
        		try {
					ais.skip(BYTES_PER_BUFFER);
				} catch (IOException e) {
					logger.error("Error skipping forwards on input stream",e);
				}
        	}
        }
    } 
 
}