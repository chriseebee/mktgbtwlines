package uk.me.chriseebee.mktgbtwlines2.audio;

import java.io.Serializable;
import java.util.Date;


public class TimedAudioBuffer implements Serializable {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int BYTES_PER_BUFFER = 16000; //buffer size in bytes
	  // For LINEAR16 at 16000 Hz sample rate, 16000 bytes corresponds to 500 milliseconds of audio.
	  private byte[] buffer = new byte[BYTES_PER_BUFFER];
	  
	  private long startDateTime = 0;
	  private long endDateTime = 0;
	  
	  // As all will be 500ms, no need for endDateTime
	  
	public TimedAudioBuffer(long startDateTime) {
		this.startDateTime=startDateTime;
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

	public void setStartDateTime(long startDateTime) {
		this.startDateTime = startDateTime;
	}
	
	public long getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(long endDateTime) {
		this.endDateTime = endDateTime;
	}
	
	public double getLength() {
		if (this.endDateTime>0 && this.startDateTime>0) {
			return this.endDateTime-this.startDateTime;
		} else {
			return -1;
		}
	}

}
