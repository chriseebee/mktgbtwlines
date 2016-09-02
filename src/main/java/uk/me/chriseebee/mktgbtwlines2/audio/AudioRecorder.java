package uk.me.chriseebee.mktgbtwlines2.audio;

import javax.sound.sampled.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;
import uk.me.chriseebee.mktgbtwlines2.nlp.Transcription;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
 
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
    private static final int BYTES_PER_BUFFER = 640000; //buffer size in bytes

    private volatile boolean running = true;
    
    // For LINEAR16 at 16000 Hz sample rate, 320000 bytes corresponds to 10 seconds of audio.
    byte[] buffer = new byte[BYTES_PER_BUFFER];
    
    AudioUtils au;
    // format of audio file for Wave
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
    TargetDataLine line;
    
    private  ConcurrentLinkedQueue<Date> queue = null;
    
    public AudioRecorder() {
    	
    	au = new AudioUtils();
    	au.setupRecording();
    	queue = ThreadCommsManager.getInstance().getNoiseDetectionQueue();
    }

    /**
     * Captures the sound and record into a WAV file
     */
    
    private void record(Date date) {
    	//if (ThreadCommsManager.getInstance().isRecording()) {
        	TimedAudioBuffer tab = new TimedAudioBuffer(date);
        	int bytesRead = 0;
			try {
				logger.info("Starting to record buffer");
				bytesRead = au.getAudioInputStream().read(buffer);
				logger.info("Finished record buffer");
			} catch (IOException e1) {
				logger.error("Error reading from input stream",e1);
			}
        	if (bytesRead>0) {
            	tab.setEndDateTime(new Date());
            	tab.setBuffer(buffer);
            	logger.info("Putting Audio Buffer on Queue");
            	ThreadCommsManager.getInstance().getAudioBufferQueue().add(tab);
        	} 
        	
        	ThreadCommsManager.getInstance().setRecording(false);
    	//} 
//    	else {
//    		try {
//				ais.skip(BYTES_PER_BUFFER);
//			} catch (IOException e) {
//				logger.error("Error skipping forwards on input stream",e);
//			}
//    	}
    }
    
    public void run() {
	    while ( running ) {
	    	Date d = queue.poll();
	    	if (d!=null) {
	    		logger.info("Noise Detected: Audio Recorder about to record");
		    	ThreadCommsManager.getInstance().setRecording(true);
		        this.record(d);
	    	}
	    }
    } 
 
    public void stopRunning() {
    	this.running = false;
    }    
}