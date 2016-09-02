package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.audio.TimedAudioBuffer;
import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;


public class NLPListenerThread extends Thread {
	
	Logger logger = LoggerFactory.getLogger(NLPListenerThread.class);

    private final ConcurrentLinkedQueue<Transcription> queue;
    private volatile boolean running = true;
    NLPPipeline nlpClient = null;
    
    public NLPListenerThread() {
    	nlpClient = new NLPPipeline();
        this.queue = ThreadCommsManager.getInstance().getNlpMessageQueue();
    }
	  
    public void run() {
        while ( running ) {
        	Transcription transcription = queue.poll();
            if (transcription!=null) {
            	nlpClient.processText(transcription);
            }
        }
    }
    
    public void stopRunning() {
    	this.running = false;
    }    
    
}
