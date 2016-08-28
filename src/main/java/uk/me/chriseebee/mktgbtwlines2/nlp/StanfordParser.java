package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.util.Collection;
import java.util.List;
import java.io.StringReader;

import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class StanfordParser {



	  /**
	   * The main method demonstrates the easiest way to load a parser.
	   * Simply call loadModel and specify the path of a serialized grammar
	   * model, which can be a file, a resource on the classpath, or even a URL.
	   * For example, this demonstrates loading a grammar from the models jar
	   * file, which you therefore need to include on the classpath for ParserDemo
	   * to work.
	   *
	   * Usage: {@code java ParserDemo [[model] textFile]}
	   * e.g.: java ParserDemo edu/stanford/nlp/models/lexparser/chineseFactored.ser.gz data/chinese-onesent-utf8.txt
	   *
	   */
	  public static void main(String[] args) {
	    String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	    if (args.length > 0) {
	      parserModel = args[0];
	    }
	    LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);

	    StanfordParser sp  = new StanfordParser();
	      //demoAPI(lp, "Harris likes Weetabix and other cereals more than he does any other food");
	    sp.simplePosTagger("Harris likes Weetabix and other cereals more than he does any other food");
	  }


	  
	  public void simplePosTagger(String text) {
	       // Initialize the tagger
	        MaxentTagger tagger = new MaxentTagger("models/english-left3words-distsim.tagger");
	 
	        Sentence sen = new Sentence (text);
	        List<String> strTokens = sen.nerTags();
	        List<String> posTokens = sen.posTags();

	        int i=0;
	        for (String str : posTokens){
	        	if (str.startsWith("NN")) {
	        		System.out.println("Word: "+ sen.word(i)+" is a NN");
	        		if (posTokens)
	        	}
	        	i++;
	        }
//	        
//	        
//	        // Create a document. No computation is done yet.
//	        Document doc = new Document("add your text here! It can contain multiple sentences.");
//	        for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences
//	            // We're only asking for words -- no need to load any models yet
//	            System.out.println("The second word of the sentence '" + sent + "' is " + sent.word(1));
//	            // When we ask for the lemma, it will load and run the part of speech tagger
//	            System.out.println("The third lemma of the sentence '" + sent + "' is " + sent.lemma(2));
//	            // When we ask for the parse, it will load and run the parser
//	            System.out.println("The parse of the sentence '" + sent + "' is " + sent.parse());
//	            // ...
//	        }
//	        
//	     // print the adjectives in one more sentence. This shows how to get at words and tags in a tagged sentence.
//	        List<HasWord> sent = Sentence.toWordList(text);
//	        List<TaggedWord> taggedSent = tagger.tagSentence(sent);
//	        for (TaggedWord tw : taggedSent) {
//	          if (tw.tag().startsWith("JJ")) {
//	            pw.println(tw.word());
//	          }
//	        }
//	
//	        // The tagged string
//	       // String tagged = tagger.tagString(text);
//	        Sentence sen = new Sentence(text);
//
//	        Sentence taggedSentence = (Sentence) tagger.tagSentence((List<? extends HasWord>) sen);
//	        
//	        ArrayList<TaggedWord> ts = tagger.apply(sen);
//	 
//	        // Output the result
//	        System.out.println(tagged);
	   
	  }

	  private StanfordParser() {} // static methods only
}
