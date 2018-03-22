package uk.me.chrismbell.text_classify;

import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.guillaumenargeot.ngramsfrequency.NGram;
import com.github.guillaumenargeot.ngramsfrequency.NGramsFrequency;

public class NGramExtractor {

	
	String allWords = "";
	static Logger logger = LoggerFactory.getLogger(NGramExtractor.class);


    public void testFromLines(Iterable<String> lines) {
    	
    	int topN = 10;
    	
    	final Iterable<Map.Entry<NGram, Integer>> topFivegrams = NGramsFrequency.fromLines(lines, 5).topK(topN);
    	
    	logger.debug("----------------------");
    	logger.debug("-- 5");
    	logger.debug("----------------------");
    	allWords = processNgrams(allWords,topFivegrams);
    	
    	final Iterable<Map.Entry<NGram, Integer>> topFourgrams = NGramsFrequency.fromLines(lines, 4).topK(topN);
    	
    	logger.debug("----------------------");
    	logger.debug("-- 4");
    	logger.debug("----------------------");
    	allWords = processNgrams(allWords,topFourgrams);
    	
    	logger.debug("ALL WORDS="+allWords);
    	
    	final Iterable<Map.Entry<NGram, Integer>> topTrigrams = NGramsFrequency.fromLines(lines, 3).topK(topN);
    	
    	logger.debug("----------------------");
    	logger.debug("-- 3");
    	logger.debug("----------------------");
    	allWords = processNgrams(allWords,topTrigrams);
    	
    	final Iterable<Map.Entry<NGram, Integer>> topBigrams = NGramsFrequency.fromLines(lines, 2).topK(topN);
    	
    	logger.debug("----------------------");
    	logger.debug("-- 2");
    	logger.debug("----------------------");
    	allWords = processNgrams(allWords,topBigrams);
    	
    	final Iterable<Map.Entry<NGram, Integer>> topUnigrams = NGramsFrequency.fromLines(lines, 1).topK(topN);  
    	logger.debug("----------------------");
    	logger.debug("-- 1");
    	logger.debug("----------------------");    	
    	allWords = processNgrams(allWords,topUnigrams);
    	
    	logger.info("ALL WORDS="+allWords.replaceAll("\\, \\, ", ""));
    }
    
    private static String processNgrams(String allWords, Iterable<Map.Entry<NGram, Integer>> ngrams) {
    	
    	String s = new String(allWords);
    	for (Map.Entry<NGram,Integer> entry: ngrams) {
    		
    		if (entry.getValue()>1) {
	   	 		// Iterate over the entry and keep removing sections that exist in the big string
    			String x = removeWords(s,entry.getKey().value());
    			s = s +", "+  x.trim();
    		}
    	}	
    	return s;
    }
    
    private static String removeWords(String bigString, String littleString) {
    	
    	logger.debug("Phrase = "+littleString);
   		
		String overlap = Overlap.overlap2(bigString, littleString);
		
		logger.debug("Overlap="+overlap);
		
		int previousLength = littleString.length();
		String newWord = littleString.replaceAll(Pattern.quote(overlap), "").trim();
		int loopCounter = 0;
		System.out.println("Old/New Lengths="+previousLength + "/" + newWord.length());
		while ((newWord.length() > 0) && (previousLength > newWord.length()) && loopCounter<10) {
			logger.debug("Iterating");
			newWord = removeWords (bigString,newWord);
			logger.debug("Remaining Words to Add to All Words="+newWord);
			loopCounter++;
		}
		
		return newWord;
    }
    
    public static void main(String[] args) {
    	System.out.println("STARTING");
    	System.out.println(NGramExtractor.removeWords("This is a long phrase that we need to stem","a long phrase stem"));
    	System.out.println("ENDING");
    }
}
