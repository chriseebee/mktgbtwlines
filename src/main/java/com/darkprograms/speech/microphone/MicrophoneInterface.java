package com.darkprograms.speech.microphone;

public interface MicrophoneInterface {

	public int getFrequency(int numOfBytes) throws Exception;
	
	public byte[] getBytes(int numOfBytes);
	
	void open();
	
	void close();
}
