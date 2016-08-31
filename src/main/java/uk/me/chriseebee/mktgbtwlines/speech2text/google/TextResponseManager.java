package uk.me.chriseebee.mktgbtwlines.speech2text.google;


import com.google.cloud.speech.v1beta1.StreamingRecognizeResponse;
import com.google.cloud.speech.v1beta1.StreamingRecognitionResult;
import com.google.cloud.speech.v1beta1.SpeechRecognitionAlternative;

public class TextResponseManager {

	public TextResponseManager() {
		
	}
	

    public String acceptResponse(StreamingRecognizeResponse response) {
    	
	    for (StreamingRecognitionResult res: response.getResultsList()) {
	       System.out.println("IsFinal = "+res.getIsFinal());
	       if (res.getIsFinal()) {
	    	   for (SpeechRecognitionAlternative sra: res.getAlternativesList()) {
		    	   System.out.println("*** Transcript = "+sra.getTranscript());
		       }
		       return true;
	       }
	    }
	    
	    return false;
    }

}
