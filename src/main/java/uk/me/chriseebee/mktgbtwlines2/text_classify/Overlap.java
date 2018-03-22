package uk.me.chrismbell.text_classify;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Overlap {
	
	static Logger logger = LoggerFactory.getLogger(Overlap.class);

    static int calculateOverlap(final String s1, final String s2) {
        final int s1len = s1.length();
        final int s2len = s2.length();
        final int maxlen = Integer.min(s1len, s2len);

        // from the longest overlap to the shortest possible.
        for (int len = maxlen; len > 0; len--) {
            // sliding window into s1 from 0
            for (int toffset = 0; toffset + len <= s1len; toffset++) {
                // sliding window into s2 from 0
                for (int ooffset = 0; ooffset + len <= s2len; ooffset++) {
                    //System.out.println("Comparing s1.regionMatches(" + toffset + ", s2, " + ooffset + ", " + len + ")");
                    if (s1.regionMatches(toffset, s2, ooffset, len)) {
                        return len;
                    }
                }
            }
        }

        // no overlap found.
        return 0;
    }
    
    
    public static String overlap2(String s1, String s2) {
    	String[] firstSentenceWords = s1.replaceAll("[.,]", "").split(" ");
	    Set<String> overlappingPhrases = new HashSet<String>();     
	    String lastPhrase = "";     
	    for(String word : firstSentenceWords){
	        if(lastPhrase.isEmpty()){
	            lastPhrase = word;
	        }else{
	            lastPhrase = lastPhrase + " " + word;
	        }
	        if(s2.contains(word)){
	            overlappingPhrases.add(word);
	            if(s2.contains(lastPhrase)){
	                overlappingPhrases.add(lastPhrase);
	            }
	        }else{
	            lastPhrase = "";
	        }
	    }
	    //System.out.println(overlappingPhrases);
	    return getLongestOverlappingString(overlappingPhrases);
	}
    
    private static String getLongestOverlappingString(Set<String> phrases) {
    	String longestString = "";
    	int longestLength = 0;
    	
    	for (String s: phrases) {
    		//System.out.println(s + ":"+s.length());
    		if (s.length()>longestLength) {
    			longestString = s;
    			longestLength = s.length();
    		}
    	}
    	
    	return longestString;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //String s1 = "domestic & general services";
        //String s2 = "& general bank statement";
        //System.out.println("Maximum overlap is: " + calculateOverlap(s1, s2));
        //String f1 = "domestic & general";
        //String f2 = "general services";
        //System.out.println("Maximum overlap is: " + calculateOverlap(f1, f2));
        
        
        //String g1 = "The dog is nice to my cat";
        //String g2 = "dog is nice";
        
        String h1=",domestic & general services, leading specialist warranty provider,, services";
        String h2="leading specialist warranty";
        
        
        //System.out.println(overlap2(s1,s2));
        //System.out.println(overlap2(f1,f2));
        //System.out.println(overlap2(g1,g2));
        
        logger.debug(overlap2(h1,h2));
    }
}