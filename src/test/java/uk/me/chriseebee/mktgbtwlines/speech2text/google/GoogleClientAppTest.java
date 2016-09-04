package uk.me.chriseebee.mktgbtwlines.speech2text.google;


import io.grpc.ManagedChannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines.speech2text.TestFilesSetup;

import com.examples.cloud.speech.AsyncRecognizeClient;
import com.google.cloud.speech.v1beta1.StreamingRecognitionResult;
import com.google.cloud.speech.v1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1beta1.StreamingRecognizeResponse;

public class GoogleClientAppTest {

	  Logger logger = LoggerFactory.getLogger(GoogleClientAppTest.class);
	  
	  //private List<String> transcriptList = new ArrayList<String>();
	
	  @Test
	  public void testFiles() throws InterruptedException, IOException {
		  
		  TestFilesSetup tfs = new TestFilesSetup();

	    String host = "speech.googleapis.com";
	    int port = 443;
	    
	    ManagedChannel channel = AsyncRecognizeClient.createChannel(host, port);
	    
	    for (int i=0;i<3;i++) {
			logger.info("File "+i);
			URI uri = null;
			try {
				logger.info("Testing File Name = "+tfs.getFileNameList().get(i));
				uri = this.getClass().getResource("/"+tfs.getFileNameList().get(i)).toURI();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    StreamingRecognizeClient2 client = new StreamingRecognizeClient2(channel, 16000);
		    
		    client.setup();
		    FileInputStream fis = new FileInputStream(new File(uri));
		    
		    client.recognize(fis, new Date());
		    
		    List<StreamingRecognizeResponse> responses = client.getResponses();
		    processResponse(responses);
		}

	  }
	  
	  
	  private void processResponse(List<StreamingRecognizeResponse> responses) {
		  

		for (StreamingRecognizeResponse r : responses) { 
		    for (StreamingRecognitionResult res: r.getResultsList()) {
		       //System.out.println("IsFinal = "+res.getIsFinal());
		       if (res.getIsFinal()) {
			       for (SpeechRecognitionAlternative sra: res.getAlternativesList()) {
			    	   logger.info("*** Transcript = "+sra.getTranscript());
			       }
			       return;
		       }
		    }
		}	    
	    return;
    }
 }

