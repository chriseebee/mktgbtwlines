package uk.me.chriseebee.mktgbtwlines2.audio;

import static org.junit.Assert.*;

import javax.sound.sampled.AudioFormat;

import org.junit.Test;

public class AudioUtilsTest {

	@Test
	public void testAudioFormatFrameLength() {
		
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false; // big endian has a bug
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        
        assertTrue(format.getFrameSize()==2);
	}

}
