package uk.me.chriseebee.mktgbtwlines2.nlp;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

import java.io.IOException;
import java.util.List;


public class CustomNamedEntityRecognizer {

	public CustomNamedEntityRecognizer() {
		
	}
	
	public void classify(String text) {
		
		String serializedClassifier = "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz";

	    AbstractSequenceClassifier<CoreLabel> classifier = null;
		try {
			classifier = CRFClassifier.getClassifier(serializedClassifier);
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	      String[] example = {"Good afternoon Rajat Raina, how are you today?",
	                          "I go to school at Stanford University, which is located in California." };
	      for (String str : example) {
	        System.out.println(classifier.classifyToString(str));
	      }
	      System.out.println("---");

	      for (String str : example) {
	        // This one puts in spaces and newlines between tokens, so just print not println.
	        System.out.print(classifier.classifyToString(str, "slashTags", false));
	      }
	      System.out.println("---");

	      for (String str : example) {
	        // This one is best for dealing with the output as a TSV (tab-separated column) file.
	        // The first column gives entities, the second their classes, and the third the remaining text in a document
	        System.out.print(classifier.classifyToString(str, "tabbedEntities", false));
	      }
	      System.out.println("---");

	      for (String str : example) {
	        System.out.println(classifier.classifyWithInlineXML(str));
	      }
	      System.out.println("---");

	      for (String str : example) {
	        System.out.println(classifier.classifyToString(str, "xml", true));
	      }
	      System.out.println("---");

	      for (String str : example) {
	        System.out.print(classifier.classifyToString(str, "tsv", false));
	      }
	      System.out.println("---");

	      // This gets out entities with character offsets
	      int j = 0;
	      for (String str : example) {
	        j++;
	        List<Triple<String,Integer,Integer>> triples = classifier.classifyToCharacterOffsets(str);
	        for (Triple<String,Integer,Integer> trip : triples) {
	          System.out.printf("%s over character offsets [%d, %d) in sentence %d.%n",
	                  trip.first(), trip.second(), trip.third, j);
	        }
	      }
	      System.out.println("---");

	      // This prints out all the details of what is stored for each token
	      int i=0;
	      for (String str : example) {
	        for (List<CoreLabel> lcl : classifier.classify(str)) {
	          for (CoreLabel cl : lcl) {
	            System.out.print(i++ + ": ");
	            System.out.println(cl.toShorterString());
	          }
	        }
	      }

	      System.out.println("---");

	    }


	public static void main(String[] args) {
		CustomNamedEntityRecognizer ner = new CustomNamedEntityRecognizer();
		ner.classify();
	}
}
