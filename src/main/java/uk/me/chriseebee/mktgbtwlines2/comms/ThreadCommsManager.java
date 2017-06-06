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
	
	
	private ConcurrentLinkedQueue<TimedAudioBuffer> audioBufferQueue = new ConcurrentLinkedQueue<TimedAudioBuffer>();

	private ConcurrentLinkedQueue<TimedAudioBuffer> sentenceInSpeechDetectionQueue = new ConcurrentLinkedQueue<TimedAudioBuffer>();
	
	private ConcurrentLinkedQueue<InterestingEvent> storageMessageQueue = new ConcurrentLinkedQueue<InterestingEvent>();
	
	private ConcurrentLinkedQueue<Transcription> nlpMessageQueue = new ConcurrentLinkedQueue<Transcription>();

	public boolean isRecording() {
		return recording;
	}

	public void setRecording(boolean recording) {
		this.recording = recording;
	}

	public ConcurrentLinkedQueue<TimedAudioBuffer> getAudioBufferQueue() {
		return audioBufferQueue;
	}

	public ConcurrentLinkedQueue<TimedAudioBuffer> getSentenceInSpeechDetectionQueue() {
		return sentenceInSpeechDetectionQueue;
	}

	public ConcurrentLinkedQueue<InterestingEvent> getStorageMessageQueue() {
		return storageMessageQueue;
	}


	public ConcurrentLinkedQueue<Transcription> getNlpMessageQueue() {
		return nlpMessageQueue;
	}


	
}