package uk.me.chriseebee.mktgbtwlines2;


public class TimedAudioBuffer {

	  private static final int BYTES_PER_BUFFER = 3200; //buffer size in bytes
	  // For LINEAR16 at 16000 Hz sample rate, 3200 bytes corresponds to 100 milliseconds of audio.
	  private byte[] buffer = new byte[BYTES_PER_BUFFER];
	  
	  private long startDateTime;
	  
	  // As all will be 100ms, no need for endDateTime
	  
	public TimedAudioBuffer(byte[] buf, long startDateTime) {
		this.buffer = buf;
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
	
	

}
