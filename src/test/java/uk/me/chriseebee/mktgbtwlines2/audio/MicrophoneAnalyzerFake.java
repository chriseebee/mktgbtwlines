/**
 * 
 */
package uk.me.chriseebee.mktgbtwlines2.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.darkprograms.speech.microphone.MicrophoneAnalyzer;

import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;

/**
 * @author cbell
 *
 */
public class MicrophoneAnalyzerFake extends MicrophoneAnalyzer  {

	Logger logger = LoggerFactory.getLogger(MicrophoneAnalyzerFake.class);
	FileInputStream fis;
	SourceDataLine sdl;
	/**
	 * 
	 */
	public MicrophoneAnalyzerFake() {
		super(); 
		
        if (AudioSystem.isLineSupported(Port.Info.HEADPHONE) || AudioSystem.isLineSupported(Port.Info.SPEAKER)) {
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, getAudioFormat());
            try {
				sdl = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			} catch (LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            try {
				sdl.open(getAudioFormat());
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            sdl.start();
            
        } else {
        	logger.error("Speaker not supported!");
        }
        
	}

	public void setupFile(File f) throws FileNotFoundException {
		fis = new FileInputStream(f);
	}

	/**
	 * Returns the a byte[] containing the specified number of bytes
	 * @param numOfBytes The length of the returned array.
	 * @return The specified array or null if it cannot.
	 */
	@Override
	public byte[] getBytes(int numOfBytes){
		
		logger.debug("In getBytes("+numOfBytes+")");
		byte[] data = new byte[numOfBytes];
		try {
			fis.read(data, 0, numOfBytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//feedToSpeaker(data);
		
    	TimedAudioBuffer tab = new TimedAudioBuffer(System.currentTimeMillis());
    	if (data.length>0) {
        	tab.setEndDateTime(System.currentTimeMillis());
        	tab.setBuffer(data);
        	logger.debug("Putting Audio Buffer on Queue");
        	ThreadCommsManager.getInstance().getAudioBufferQueue().add(tab);
    	} 

		return data;
	}
	
	private void feedToSpeaker(byte[] data) {
		
		if (data.length>0) {
			sdl.write(data,0,data.length);
			sdl.drain();
		}
	}
	
	/**
	 * Calculates the frequency based off of the number of bytes. 
	 * CAVEAT: THE NUMBER OF BYTES MUST BE A MULTIPLE OF 2!!!
	 * @param numOfBytes The number of bytes which must be a multiple of 2!!!
	 * @return The calculated frequency in Hertz.
	 */
	public int getFrequency(int numOfBytes) throws Exception{
		
		logger.info("In getFrequency");
		byte[] data = new byte[numOfBytes+1]; //One byte is lost during conversion
    	fis.read(data, 0, numOfBytes);
		return getFrequency(data);
	}
	
    /**
     * Gets the volume of the microphone input
     * @param interval: The length of time you would like to calculate the volume over in milliseconds.
     * @return The volume of the microphone input or -1 if data-line is not available. 
     */    
    public int getAudioVolume(int interval){
    	return super.getAudioVolume(interval);
    }

    public void close() {
    	super.close();
    	sdl.close();
    }
    
	public void sleepThread(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			logger.error("Error Occured in thread sleep",e);
		}	
	}
	
}
