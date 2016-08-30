package uk.me.chriseebee.mktgbtwlines2.comms;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.audio.TimedAudioBuffer;
import uk.me.chriseebee.mktgbtwlines2.nlp.InterestingEvent;

public class ThreadCommsManager {
	
	Logger logger = LoggerFactory.getLogger(ThreadCommsManager.class);
	
	private static ThreadCommsManager _instance = null;
	
    private boolean continueRecording = false;
	
	private ThreadCommsManager() {}
	
	public static ThreadCommsManager getInstance () {
		if (_instance == null) {
			_instance = new ThreadCommsManager();
		}
		
		return _instance;
	}
	

	private ConcurrentLinkedQueue<TimedAudioBuffer> audioBufferQueue = new ConcurrentLinkedQueue<TimedAudioBuffer>();
	
	private ConcurrentLinkedQueue<InterestingEvent> influxMessageQueue = new ConcurrentLinkedQueue<InterestingEvent>();

	public boolean isContinueRecording() {
		return continueRecording;
	}

	public void setLaughing(boolean continueRecording) {
		this.continueRecording = continueRecording;
	}

	public ConcurrentLinkedQueue<TimedAudioBuffer> getAudioBufferQueue() {
		return audioBufferQueue;
	}

	public ConcurrentLinkedQueue<InterestingEvent> getInfluxMessageQueue() {
		return influxMessageQueue;
	}
	
}