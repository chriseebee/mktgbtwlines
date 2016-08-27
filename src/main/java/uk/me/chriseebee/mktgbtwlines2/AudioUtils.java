package uk.me.chriseebee.mktgbtwlines2;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class AudioUtils {

	AudioInputStream ais;
	
	public AudioUtils() {
		
	}
	
	public  void printMixers() {
		  Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
	      System.out.println("Available mixers:");
	      for(int cnt = 0; cnt < mixerInfo.length;cnt++){
	      	System.out.println(mixerInfo[cnt].getName());
	      }//end for loop
	}
	
	public  void  printMixerSupport (Mixer mixer){

		try {
			mixer.open();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.printf("Supported SourceDataLines of mixer (%s):\n\n", mixer.getMixerInfo().getName());
		for(Line.Info info : mixer.getTargetLineInfo()) {
		    if(TargetDataLine.class.isAssignableFrom(info.getLineClass())) {
		        TargetDataLine.Info info2 = (TargetDataLine.Info) info;
		        System.out.println(info2);
		        System.out.printf("  max buffer size: \t%d\n", info2.getMaxBufferSize());
		        System.out.printf("  min buffer size: \t%d\n", info2.getMinBufferSize());
		        AudioFormat[] formats = info2.getFormats();
		        System.out.println("  Supported Audio formats: ");
		        for(AudioFormat format : formats) {
		            System.out.println("    "+format);
//		          System.out.printf("      encoding:           %s\n", format.getEncoding());
//		          System.out.printf("      channels:           %d\n", format.getChannels());
//		          System.out.printf(format.getFrameRate()==-1?"":"      frame rate [1/s]:   %s\n", format.getFrameRate());
//		          System.out.printf("      frame size [bytes]: %d\n", format.getFrameSize());
//		          System.out.printf(format.getSampleRate()==-1?"":"      sample rate [1/s]:  %s\n", format.getSampleRate());
//		          System.out.printf("      sample size [bit]:  %d\n", format.getSampleSizeInBits());
//		          System.out.printf("      big endian:         %b\n", format.isBigEndian());
//		          
//		          Map<String,Object> prop = format.properties();
//		          if(!prop.isEmpty()) {
//		              System.out.println("      Properties: ");
//		              for(Map.Entry<String, Object> entry : prop.entrySet()) {
//		                  System.out.printf("      %s: \t%s\n", entry.getKey(), entry.getValue());
//		              }
//		          }
		        }
		        System.out.println();
		    } else {
		        System.out.println(info.toString());
		    }
		    System.out.println();
		}

		mixer.close();
	}
	
	
    public AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false; // big endian has a bug
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return format;
    }
    
	public void startRecording() {
	    // the line from which audio data is captured
	    TargetDataLine line;
	    
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
 
            System.out.println("Start capturing...");
 
            ais = new AudioInputStream(line);
 
           
            System.out.println("Start recording...");
          
 
            // start recording
            //AudioSystem.write(ais, fileType, wavFile);
            //File f = new File("/Users/cbell/Desktop/temp.flac");
            //AudioSystem.write(ais,  FLACFileWriter.FLAC, f );
 
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

	public AudioInputStream getAudioInputStream() {
		return ais;
	}
}