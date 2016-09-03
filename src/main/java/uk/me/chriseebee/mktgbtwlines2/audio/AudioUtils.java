package uk.me.chriseebee.mktgbtwlines2.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AudioUtils {

	Logger logger = LoggerFactory.getLogger(AudioUtils.class);
	
	AudioInputStream ais;
	TargetDataLine line;
	
	public AudioUtils() {
		
	}
	
	public  void printMixers() {
		  Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
	      logger.info("Available mixers:");
	      for(int cnt = 0; cnt < mixerInfo.length;cnt++){
	    	  logger.info(mixerInfo[cnt].getName());
	      }//end for loop
	} 
	
	public  void  printMixerSupport (Mixer mixer){

		try {
			mixer.open();
		} catch (LineUnavailableException e) {
			logger.error("Cannot open Mixer",e);
		}

		System.out.printf("Supported SourceDataLines of mixer (%s):\n\n", mixer.getMixerInfo().getName());
		for(Line.Info info : mixer.getTargetLineInfo()) {
		    if(TargetDataLine.class.isAssignableFrom(info.getLineClass())) {
		        TargetDataLine.Info info2 = (TargetDataLine.Info) info;
		        //logger.info(info2);
		        //logger.info("  max buffer size: \t%d\n", info2.getMaxBufferSize());
		        //logger.info("  min buffer size: \t%d\n", info2.getMinBufferSize());
		        AudioFormat[] formats = info2.getFormats();
		        logger.info("  Supported Audio formats: ");
		        for(AudioFormat format : formats) {
		          logger.info("    "+format);
//		          logger.info("      encoding:           %s\n", format.getEncoding());
//		          logger.info("      channels:           %d\n", format.getChannels());
//		          logger.info(format.getFrameRate()==-1?"":"      frame rate [1/s]:   %s\n", format.getFrameRate());
//		          logger.info("      frame size [bytes]: %d\n", format.getFrameSize());
//		          logger.info(format.getSampleRate()==-1?"":"      sample rate [1/s]:  %s\n", format.getSampleRate());
//		          logger.info("      sample size [bit]:  %d\n", format.getSampleSizeInBits());
//		          logger.info("      big endian:         %b\n", format.isBigEndian());
//		          
//		          Map<String,Object> prop = format.properties();
//		          if(!prop.isEmpty()) {
//		              logger.info("      Properties: ");
//		              for(Map.Entry<String, Object> entry : prop.entrySet()) {
//		                  logger.info("      %s: \t%s\n", entry.getKey(), entry.getValue());
//		              }
//		          }
		        }
		        logger.info("");
		    } else {
		    	logger.info(info.toString());
		    }
		    logger.info("");
		}

		mixer.close();
	}
	
	
    public static AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false; // big endian has a bug
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        
        return format;
    }
    
	public void setupRecording() {
	    
        try {
            AudioFormat format = getAudioFormat();
            
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
 
            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            
           printMixers();
            
            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
            Mixer mixer = AudioSystem.getMixer(mixerInfo[0]); 
     
           printMixerSupport (mixer);
            
            line = (TargetDataLine)mixer.getLine(info);
            line.open(format);
            line.start();   // start capturing
 
            logger.info("Start capturing...");
 
            ais = new AudioInputStream(line);
 
        } catch (LineUnavailableException ex) {
            logger.error("Line Unavailable",ex);
        }
    }

	public AudioInputStream getAudioInputStream() {
		return ais;
	}
	
    /**
     * Closes the target data line to finish capturing and recording
     */
    public void stopRecording() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }
}