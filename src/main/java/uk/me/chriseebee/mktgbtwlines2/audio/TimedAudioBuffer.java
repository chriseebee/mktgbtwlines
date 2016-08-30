package uk.me.chriseebee.mktgbtwlines2.audio;

import java.util.Date;


public class TimedAudioBuffer {

	  private static final int BYTES_PER_BUFFER = 3200; //buffer size in bytes
	  // For LINEAR16 at 16000 Hz sample rate, 3200 bytes corresponds to 100 milliseconds of audio.
	  private byte[] buffer = new byte[BYTES_PER_BUFFER];
	  
	  private long startDateTime;
	  private long endDateTime;
	  
	  // As all will be 100ms, no need for endDateTime
	  
	public TimedAudioBuffer(Date startDateTime) {
		this.startDateTime=startDateTime.getTime();
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public long getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime.getTime();
	}
	
	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime.getTime();
	}
	
	

}
