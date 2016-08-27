package uk.me.chriseebee.mktgbtwlines2;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class NLPPipeline {

	public NLPPipeline() {
		
	}
	
	public void processText (String chunk, long startTime) {
		// The text coming in here needs breaking into sentences
		StringReader reader = new StringReader(chunk);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
	      for (List<HasWord> sentence : dp) {

	    	  // Now let's parse the sentence
	    	
	    	  // 
	      }
	}
	
	/**
	 * 
	 * @param chunk
	 * @param dateTimeMsAsString - this is the time the recording was made in Milliseconds since epoch
	 */
	public void processText2 (String chunk, String dateTimeMsAsString) {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(chunk);

		// run all Annotators on this text
		pipeline.annotate(document);
		
		NamedEntityManager nem = new NamedEntityManager();
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		List<String> consecutiveNouns = new ArrayList<String>();
		String nounType = null;
		InterestingEvent ev = null;
		
		for(CoreMap sentence: sentences) {
			System.out.println(sentence);
		  // traversing the words in the current sentence
		  // a CoreLabel is a CoreMap with additional token-specific methods
		  for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
		    // this is the text of the token
		    String word = token.get(TextAnnotation.class);
		    // this is the POS tag of the token
		    String pos = token.get(PartOfSpeechAnnotation.class);
		    // this is the NER label of the token
		    String ne = token.get(NamedEntityTagAnnotation.class);
		    
		    if (pos.startsWith("NN%")) {
		    	
		    	// this is interesting to us, so let's init the interesting event
		    	if (ev==null) { ev = new InterestingEvent(); }
		    	
		    	// So it's possible that this word is a single product or 
		    	// brand, 
		    	// It could also be part of a multiple word product/brand 
		    	
		    	// First let's check that.
		    	nounType = nem.isWordRecognized(word, nounType);
		    	// If it is, then add it to a list for further checking based on further words
		    	if (nounType!=null) {
		    		consecutiveNouns.add(word);
		    	}
		    } else {
		    	// So the word is not a noun. 
		    	// Are there any in the buffer that are so that we can finalise them?
		    	if (consecutiveNouns.size()>=1) {
		    		// Yes, is this a single word or multiple
		    		if (consecutiveNouns.size()==1) { 
		    			// this is a single word
		    			ev.setIdentifiedEntity(consecutiveNouns.get(0));
		    			ev.setIdentifiedEntityType(nounType);
		    		} else {
		    			
		    		}
		    	}
		    	
		    	if (pos.startsWith("JJ")) {
		    		ev.setAdjective("word");
		    	}
		    	
		    	if (pos.startsWith("VB")) {
		    		ev.setAdjective("word");
		    	}
		    }
		    
		   // System.out.println("Word = "+word + " : "+ pos + " : " + ne);
	          String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
	          ev.setSentiment(sentiment);
		  }
		  
		  // Now try and find products and brands
		  

		  // this is the parse tree of the current sentence
		  //Tree tree = sentence.get(TreeAnnotation.class);

		  // this is the Stanford dependency graph of the current sentence
		  //SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		}

	}
}
