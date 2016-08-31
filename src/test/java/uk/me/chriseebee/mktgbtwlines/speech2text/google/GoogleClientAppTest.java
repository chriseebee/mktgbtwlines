package uk.me.chriseebee.mktgbtwlines.speech2text.google;

import static org.junit.Assert.*;
import io.grpc.ManagedChannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines.speech2text.TestFilesSetup;
import uk.me.chriseebee.mktgbtwlines.speech2text.ibm.WatsonClientTest;

import com.examples.cloud.speech.AsyncRecognizeClient;
import com.google.cloud.speech.v1beta1.StreamingRecognitionResult;
import com.google.cloud.speech.v1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1beta1.StreamingRecognizeResponse;

public class GoogleClientAppTest {

	  org.slf4j.Logger logger = LoggerFactory.getLogger(WatsonClientTest.class);
	  
	  private List<String> transcriptList = new ArrayList<String>();
	  
	
	  @Test
	  public void testFiles() throws InterruptedException, IOException {

		  TestFilesSetup tfs = new TestFilesSetup();

	    String host = "speech.googleapis.com";
	    int port = 443;
	    
	    ManagedChannel channel = AsyncRecognizeClient.createChannel(host, port);
	    
	    for (int i=0;i<12;i++) {
			logger.info("File "+i);
			URI uri = null;
			try {
				uri = this.getClass().getResource("/"+tfs.getFileNameList().get(i)).toURI();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Path path = Paths.get(uri);
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

