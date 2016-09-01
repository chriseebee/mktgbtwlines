package uk.me.chriseebee.mktgbtwlines.speech2text.google;


import static org.apache.log4j.ConsoleAppender.SYSTEM_OUT;

import com.google.cloud.speech.v1beta1.RecognitionConfig;
import com.google.cloud.speech.v1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1beta1.SpeechGrpc;
import com.google.cloud.speech.v1beta1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1beta1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1beta1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;
import com.google.protobuf.TextFormat;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Client that sends streaming audio to Speech.Recognize and returns streaming transcript.
 */
public class StreamingRecognizeClient2 {

  private final String file;
  private  int samplingRate = 16000;
  private InputStream inputStream;
  private static final Logger logger = Logger.getLogger(StreamingRecognizeClient2.class.getName());

  private final ManagedChannel channel;

  private final SpeechGrpc.SpeechStub speechClient;

  private static final int BYTES_PER_BUFFER = 3200; //buffer size in bytes
  private static final int BYTES_PER_SAMPLE = 2; //bytes per sample for LINEAR16

  private static final List<String> OAUTH2_SCOPES = Arrays.asList("https://www.googleapis.com/auth/cloud-platform");
  
  //FileInputStream in = new FileInputStream(new File(file));
  // For LINEAR16 at 16000 Hz sample rate, 3200 bytes corresponds to 100 milliseconds of audio.
  byte[] buffer = new byte[BYTES_PER_BUFFER];
  int bytesRead;
  int totalBytes = 0;
  int samplesPerBuffer = BYTES_PER_BUFFER / BYTES_PER_SAMPLE;
  int samplesPerMillis = samplingRate / 1000;
  
  StreamingRecognitionConfig streamingConfig = null;
  RecognitionConfig config = null;
  //TextResponseManager trm = null;

 
  private List<StreamingRecognizeResponse> responses = new ArrayList<StreamingRecognizeResponse>();

//  /**
//   * Construct client connecting to Cloud Speech server at {@code host:port}.
//   */
//  public StreamingRecognizeClient2(ManagedChannel channel, String file, int samplingRate)
//      throws IOException {
//    this.file = file;
//    this.samplingRate = samplingRate;
//    this.channel = channel;
//    trm = new TextResponseManager();
//    
//    speechClient = SpeechGrpc.newStub(channel);
//
//    //Send log4j logs to Console
//    //If you are going to run this on GCE, you might wish to integrate with gcloud-java logging.
//    //See https://github.com/GoogleCloudPlatform/gcloud-java/blob/master/README.md#stackdriver-logging-alpha
//    
//    //ConsoleAppender appender = new ConsoleAppender(new SimpleLayout(), SYSTEM_OUT);
//    //logger.addAppender(appender);
//  }
  
  public StreamingRecognizeClient2(ManagedChannel channel,int samplingRate) throws IOException {
	    this.file=null;
	    this.samplingRate = samplingRate;
	    this.channel = channel;
	    //trm = new TextResponseManager();

	    speechClient = SpeechGrpc.newStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
  }
  
  public void setup() {      
      config = RecognitionConfig.newBuilder()
                  .setEncoding(AudioEncoding.LINEAR16)
                  .setSampleRate(samplingRate)
                  .setLanguageCode("en-GB")
                  //.setMaxAlternatives(0)
                  .build();
      streamingConfig = StreamingRecognitionConfig.newBuilder()
                  .setConfig(config)
                  .setInterimResults(false)
                  .setSingleUtterance(false)
                  .build();
  }

  public List<StreamingRecognizeResponse> getResponses() {
	  return responses;
  }
  
  /** Send streaming recognize requests to server. */
  public void recognize(InputStream is, Date date) throws InterruptedException, IOException {
    final CountDownLatch finishLatch = new CountDownLatch(1);
    StreamObserver<StreamingRecognizeResponse> responseObserver =
        new StreamObserver<StreamingRecognizeResponse>() {
          @Override
          public void onNext(StreamingRecognizeResponse response) {
           // logger.info("Received response: " + TextFormat.printToString(response));
            responses.add(response);
          }

          @Override
          public void onError(Throwable error) {
            logger.log(Level.WARN, "recognize failed: {0}", error);
            finishLatch.countDown();
          }

          @Override
          public void onCompleted() {
            logger.info("recognize completed.");
            finishLatch.countDown();
          }
        };

    StreamObserver<StreamingRecognizeRequest> requestObserver = speechClient.streamingRecognize(responseObserver);
    
    try {
      // Build and send a StreamingRecognizeRequest containing the parameters for
      // processing the audio.

      StreamingRecognizeRequest initial = StreamingRecognizeRequest.newBuilder().setStreamingConfig(streamingConfig).build();
      requestObserver.onNext(initial);

      // How to get audioinputstream duration
      // long durationInMillis = 1000 * getFrameLength() / getFormat().getFrameRate();
      
      while ((bytesRead = is.read(buffer)) != -1) {
    	  //logger.info("..");
        totalBytes += bytesRead;
        StreamingRecognizeRequest request =StreamingRecognizeRequest.newBuilder()
                .setAudioContent(ByteString.copyFrom(buffer, 0, bytesRead))
                .build();
        requestObserver.onNext(request);
        // To simulate real-time audio, sleep after sending each audio buffer.
        //Thread.sleep(samplesPerBuffer / samplesPerMillis);
        
        //logger.info("Still in loop for inputstream");
      }
      
      //logger.info("Sent " + totalBytes + " bytes from audio file: " + file);
    } catch (RuntimeException e) {
      // Cancel RPC.
      e.printStackTrace();
      requestObserver.onError(e);
      throw e;
    } catch (Exception ee) {
    	//logger.error(ee);
    	throw ee;
    }
    
    // Mark the end of requests.
    requestObserver.onCompleted();

    // Receiving happens asynchronously.
    finishLatch.await(30, TimeUnit.SECONDS);
    
  }


}