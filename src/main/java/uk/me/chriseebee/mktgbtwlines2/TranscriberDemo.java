package uk.me.chriseebee.mktgbtwlines2;


import java.io.File;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

public class TranscriberDemo {       
                                     
    public static void main(String[] args) throws Exception {
                  
    	System.out.format("Starting");
        Configuration configuration = new Configuration();

        //configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        File f = new File ("/Users/cbell/Documents/cbsap/code/cmusphinx/Sphinx-4-Acoustic-Model-Adaptation-Data-master/adaptation/en-us-adapt");
        configuration.setAcousticModelPath(f.getAbsolutePath());
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

        LiveSpeechRecognizer recognizer = new LiveSpeechRecognizer(configuration);
	     // Start recognition process pruning previously cached data.
	     recognizer.startRecognition(true);
	     SpeechResult result;
	     
	     while ((result = recognizer.getResult()) != null) {
	    	    System.out.println(result.getHypothesis());
	    }
	     //System.out.format("Hypothesis: %s\n", result.getHypothesis());
	     // Pause recognition process. It can be resumed then with startRecognition(false).
	     recognizer.stopRecognition();
	     
	     System.out.format("Stopping");
    }
}
