package uk.me.chriseebee.mktgbtwlines2.audio;

import java.io.*;
import java.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import uk.me.chriseebee.mktgbtwlines2.comms.ThreadCommsManager;

import java.io.IOException;
import javax.sound.sampled.TargetDataLine;
 
/**
 * This class records half second buffers from the audio input stream and sends them to a queue
 * to be saved in a 'store'
 * 
 * This allows the processing to be accomplished in an async fashion
 * 
 * 0.5 seconds was chosen because this is a reasonable value upon which to reconstruct sentences, given that 
 * research has shown that less than half a second of silence is likely to be 'in-sentence' and greater than 1 second
 * a post-sentence pasuse.
 * 
 * Between these two values, there is more ambiguity which we will need to consider.
 * 
 */
public class AudioStreamer extends Thread {
	
	Logger logger = LoggerFactory.getLogger(AudioStreamer.class);
	
    // record duration, in milliseconds
    //private static final long RECORD_TIME = 200000;  // 10 seconds 
    private static final int BYTES_PER_BUFFER = 16000; //buffer size in bytes

    private volatile boolean running = true;
    
    // For LINEAR16 at 16000 Hz sample rate, 16000 bytes corresponds to 0.5 seconds of audio.
    byte[] buffer = new byte[BYTES_PER_BUFFER];
    
    AudioUtils au;
    // format of audio file for Wave
    //AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
    TargetDataLine line = null;

    DatagramSocket clientSocket = null;
    ByteArrayOutputStream outToServer = null;
    //BufferedReader  = null;
    boolean connected = false;

    InetAddress ipAddress = null;
    int port = 0;

    byte[] ackPacketBuffer = new byte[1000];

    public static final String LINE = "LINE";
    public static final String FILE = "FILE";

    String lineOrFile = null;
    FileInputStream fis = null;

    public void setSourceOption(String isLineOrFile,File f) throws Exception {

        if (isLineOrFile.equals(LINE)) {
            lineOrFile  = LINE;
            line = au.setupRecording();
        } else {
            if (isLineOrFile.equals(FILE)) {
                lineOrFile  = FILE;
                try {
                    fis = new FileInputStream(f);
                } catch (FileNotFoundException fnfe) {
                    throw new Exception ("Cannot instantiate Streamer with file as does not exist: "+f.getName());
                }
            }
        }
    }
    
    public AudioStreamer(String socketHost, String socketPort) {

        logger.info("Starting Streamer");

        port = (new Integer(socketPort).intValue());
        au = new AudioUtils();
        try {
            
            //outToServer  = new ByteArrayOutputStream();
        
            logger.info("Recording Setup");
            try {
                
                clientSocket = new DatagramSocket();
                clientSocket.setSoTimeout(1000); 
                ipAddress = InetAddress.getByName(socketHost);

                logger.info("Will send data packets to: "+ ipAddress.getHostAddress() + "/" + port);

                clientSocket.connect(ipAddress,port);

                logger.info("Connected");
    
            } catch (UnknownHostException uhe) {
                logger.error("Host for Socket connection is not known",uhe);
                running = false;
            } catch (IOException ioe) {
                logger.error("IO Exception on Socket",ioe);
                running = false;
            } 
        } catch (Exception e) {
            logger.error("ERROR: "+e.getMessage());
            running = false;
        }
    }

    /**
     * Captures the sound and record into a byte buffer
     */
    
    private void record() {
        try {
            logger.info("Starting to record buffer");
            
            int bytesRead = getBytes();
            
            if (bytesRead > 0) {
                if (clientSocket.isConnected()) {
                    DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, ipAddress, port);
                    logger.info("Sending Packet");
                    clientSocket.send(sendPacket);
                    DatagramPacket ackPacket = new DatagramPacket(ackPacketBuffer, ackPacketBuffer.length);
                    clientSocket.receive(ackPacket); 
                    //outToServer.write(buffer, 0, bytesRead);
                } else {
                    logger.info("Couldn't send as not connected to server");
                }
            }
        } catch (IOException e1) {
            logger.error("Error sending packet to server",e1);
        }
    }

    private int getBytes() {
        
        int bytesRead = 0;
        if (lineOrFile.equals(LINE)) {
            bytesRead = line.read(buffer, 0, buffer.length);
        } else {
            try {
                bytesRead = fis.read(buffer, 0, buffer.length);
            } catch (IOException ioe) {
                logger.error("Can't read from file");
            }
        }
        logger.info("Got bytes from input: length="+bytesRead);
        return bytesRead;
    }
    
    public void run() {
	    while ( running ) {
		   this.record();
	    }
    } 
 
    public void stopRunning() {
        this.running = false;
        try {
            logger.info("CLOSING CONNECTIONS");
            clientSocket.close();
            line.close();
            //outToServer.close();
        } catch (Exception ioe) {
            logger.error("Error trying to close socket",ioe);
        }
    }    
}