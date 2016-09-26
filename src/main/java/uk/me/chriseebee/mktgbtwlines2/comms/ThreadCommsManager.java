package uk.me.chriseebee.mktgbtwlines2.comms;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.audio.TimedAudioBuffer;
import uk.me.chriseebee.mktgbtwlines2.nlp.InterestingEvent;
import uk.me.chriseebee.mktgbtwlines2.nlp.Transcription;

public class ThreadCommsManager {
	
	Logger logger = LoggerFactory.getLogger(ThreadCommsManager.class);
	
	private static ThreadCommsManager _instance = null;
	
    private boolean recording = false;
	
	private ThreadCommsManager() {}
	
	public static ThreadCommsManager getInstance () {
		if (_instance == null) {
			_instance = new ThreadCommsManager();
		}
		
		return _instance;
	}
	

	private ConcurrentLinkedQueue<TimedAudioBuffer> noiseDetectionQueue = new ConcurrentLinkedQueue<TimedAudioBuffer>();

	private ConcurrentLinkedQueue<TimedAudioBuffer> speechDetectionQueue = new ConcurrentLinkedQueue<TimedAudioBuffer>();
	
	private ConcurrentLinkedQueue<InterestingEvent> influxMessageQueue = new ConcurrentLinkedQueue<InterestingEvent>();
	
	private ConcurrentLinkedQueue<Transcription> nlpMessageQueue = new ConcurrentLinkedQueue<Transcription>();

	public boolean isRecording() {
		return recording;
	}

	public void setRecording(boolean recording) {
		this.recording = recording;
	}


	public ConcurrentLinkedQueue<TimedAudioBuffer> getAudioBufferQueue() {
		return speechDetectionQueue;
	}

	public ConcurrentLinkedQueue<InterestingEvent> getInfluxMessageQueue() {
		return influxMessageQueue;
	}

	public ConcurrentLinkedQueue<TimedAudioBuffer> getNoiseDetectionQueue() {
		return noiseDetectionQueue;
	}

	public ConcurrentLinkedQueue<Transcription> getNlpMessageQueue() {
		return nlpMessageQueue;
	}


	
}