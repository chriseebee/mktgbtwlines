package uk.me.chriseebee.mktgbtwlines.speech2text.cmusphinx;


import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

public class CMUSphinxClient {       
	
	static Logger logger = LoggerFactory.getLogger(CMUSphinxClient.class);
                                     
    public static void main(String[] args) throws Exception {
                  
    	logger.info("Starting");
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
	    	   logger.info(result.getHypothesis());
	    }
	     //System.out.format("Hypothesis: %s\n", result.getHypothesis());
	     // Pause recognition process. It can be resumed then with startRecognition(false).
	     recognizer.stopRecognition();
	     
	     logger.info("Stopping");
    }
}
