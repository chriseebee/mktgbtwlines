package uk.me.chriseebee.audiopitchjava;

import java.io.IOException;

import javax.sound.sampled.*;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

 
/**
 * A sample program is to demonstrate how to record sound in Java
 * author: www.codejava.net
 */
public class JavaSoundRecorder implements PitchDetectionHandler {
    // record duration, in milliseconds
    static final long RECORD_TIME = 60*1000*5;  // 5 minutes
    
    LaughterManager lm;
 
    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
    TargetDataLine line;
    private AudioDispatcher dispatcher;
    SocketClient sc;
    CollectionManager cm;
    
    static ButtonSocketServer buttonServer;
    static QueueConsumer qc;
    
    public JavaSoundRecorder (String hostname, int port) {
    	sc = new SocketClient(hostname,port);
    	cm = CollectionManager.getInstance();
    	lm = new LaughterManager(cm.queue);
    }
    
    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return format;
    }
 
    /**
     * Captures the sound and record into a WAV file
     */
    void start() {
    	
    	if(dispatcher!= null){
			dispatcher.stop();
		}
	   
		//  AudioUtils.printMixers();
	    
        try {
            AudioFormat format = getAudioFormat();
            
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
 
            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            
            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
            Mixer mixer = AudioSystem.getMixer(mixerInfo[3]); 
     
            //AudioUtils.printMixerSupport (mixer);
            line = (TargetDataLine)mixer.getLine(info);
            
            
    		float sampleRate = 16000;
    		int bufferSize = 1024;
    		int overlap = 0;
    		
            line.open(format, bufferSize);
            line.start();   // start capturing
 
            System.out.println("Start capturing...");
 
            AudioInputStream ais = new AudioInputStream(line);
 
            System.out.println("Start recording...");
            
            JVMAudioInputStream audioStream = new JVMAudioInputStream(ais);
    		// create a new dispatcher
    		dispatcher = new AudioDispatcher(audioStream, bufferSize,
    				overlap);

    		// add a processor
    		dispatcher.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, sampleRate, bufferSize, this));
    		
    		new Thread(dispatcher,"Audio dispatching").start();

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }
   
    private void sendMessageToMatrix(double timeStamp,float pitch,float probability,double rms) {
		lm.addFrequency((int)pitch);
		try {
			int lq = lm.getLaughterQuotient();
			sc.sendMessage(lq+"\n");
		} catch (IOException e) {
			System.out.println("Server Connection Closed");
			sc.closeAssets();
		}
    }
	
    public void handlePitch(PitchDetectionResult pitchDetectionResult,
			AudioEvent audioEvent) {
		
		if(pitchDetectionResult.getPitch() != -1){
			double timeStamp = audioEvent.getTimeStamp();
			java.util.Date date= new java.util.Date();
			String ts = date.getTime()+"000000";
			float pitch = pitchDetectionResult.getPitch();
			float probability = pitchDetectionResult.getProbability();
			double rms = audioEvent.getRMS() * 100;
			//String message = String.format("Pitch detected at %.2fs: %.2fHz ( %.2f probability, RMS: %.5f )\n", timeStamp,pitch,probability,rms);
			//System.out.println(message);
			this.sendMessageToMatrix(timeStamp, pitch, probability, rms);
			if (pitch<1000) {
				int laughing = CollectionManager.getInstance().isLaughing() ? 1 : 0;
				String message = "rawpitch pitch="+pitch+" "+ts;
				cm.queue.offer(message);
				String message2 = "manualval value="+laughing+" "+ts;
				cm.queue.offer(message2);
			}
		}
	}
		
    /**
     * Closes the target data line to finish capturing and recording
     */
    void finish() {
        line.stop();
        line.close();
        try {
			sc.sendMessage("STOP\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
        sc.closeAssets();
        } catch (Exception e) {}
        
        dispatcher.stop();
        buttonServer.stop();
        qc.stopRunning();
        System.out.println("Finished");
    }
    
    /**
     * Entry to run the program
     */
    public static void main(String[] args) {
  
    	String matrixHostname = "localhost";
    	String matrixPort = "0";
    	String influxHostname = "localhost";
    	
    	if (args.length==3) {
    		matrixHostname = args[0];
    		matrixPort = args[1];
    		influxHostname = args[2];
    	} else {
    		System.out.println("-------------------------");
    		System.out.println("Not enough arguments");
    		System.out.println("1 = Matrix IP Address");
    		System.out.println("2 = Matrix Port Number");
    		System.out.println("3 = InfluxDB IP Address");
    		System.out.println("-------------------------");
    	}
    	
    	int matrixPortInt = 0;
    	
    	try {
    		matrixPortInt = (new Integer(matrixPort)).intValue();
    	} catch (NumberFormatException nfe) {
    		nfe.printStackTrace();
    		System.exit(1);
    	}
    	
        final JavaSoundRecorder recorder = new JavaSoundRecorder(matrixHostname,matrixPortInt);
 
        // creates a new thread that waits for a specified
        // of time before stopping
        Thread stopper = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(RECORD_TIME);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                recorder.finish();
                
            }
        });
 
        stopper.start();
        
        ButtonSocketServer bss = new ButtonSocketServer();
        Thread t1 = new Thread (bss);
        t1.start();
        
        QueueConsumer qc = new QueueConsumer(influxHostname);
	    Thread t2 = new Thread (qc);
	    t2.start();
	    
        // start recording
        recorder.start();
    }
	
}