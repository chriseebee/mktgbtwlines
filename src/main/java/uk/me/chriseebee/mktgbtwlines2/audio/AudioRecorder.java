package uk.me.chriseebee.mktgbtwlines2.audio;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;

import java.io.IOException;
import java.util.Date;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.TargetDataLine;
 
/**
 * This class records half second buffers from the audio input stream and sends them to a queue
 * to be saved in a 'store'
 * 
 * This allows the processing to be accomplished in an async fashion
 * 
 * 0.5 seconds was chosen because this is a reasonable value upon which to reconstruct sentences, given that 
 * research has shown that less than half a second of silence is likely to be 'in-sentence' and greater than 1 second
 * a post-sentence pasuse.
 * 
 * Between these two values, there is more ambiguity which we will need to consider.
 * 
 */
public class AudioRecorder extends Thread {
	
	Logger logger = LoggerFactory.getLogger(AudioRecorder.class);
	
    // record duration, in milliseconds
    //private static final long RECORD_TIME = 200000;  // 10 seconds 
    private static final int BYTES_PER_BUFFER = 16000; //buffer size in bytes

    private volatile boolean running = true;
    
    // For LINEAR16 at 16000 Hz sample rate, 16000 bytes corresponds to 0.5 seconds of audio.
    byte[] buffer = new byte[BYTES_PER_BUFFER];
    
    AudioUtils au;
    // format of audio file for Wave
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
    TargetDataLine line;
    
    public AudioRecorder() {
    	au = new AudioUtils();
    	au.setupRecording();
    }

    /**
     * Captures the sound and record into a byte buffer
     */
    
    private void record() {
        	TimedAudioBuffer tab = new TimedAudioBuffer(System.currentTimeMillis());
        	int bytesRead = 0;
			try {
				logger.info("Starting to record buffer");
				bytesRead = au.getAudioInputStream().read(buffer);
				logger.info("Finished record buffer");
			} catch (IOException e1) {
				logger.error("Error reading from input stream",e1);
			}
        	if (bytesRead>0) {
            	tab.setEndDateTime(System.currentTimeMillis());
            	tab.setBuffer(buffer);
            	logger.info("Putting Audio Buffer on Queue");
            	ThreadCommsManager.getInstance().getAudioBufferQueue().add(tab);
        	} 
    }
    
    public void run() {
	    while ( running ) {
		   this.record();
	    }
    } 
 
    public void stopRunning() {
    	this.running = false;
    }    
}