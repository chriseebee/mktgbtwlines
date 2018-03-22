package uk.me.chriseebee.mktgbtwlines2.nlp.entity;


import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.stanford.nlp.ling.CoreAnnotations;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import uk.me.chriseebee.mktgbtwlines2.nlp.InfoExtractor2;

public class StanfordClauseExtractorTest {
	
	static InfoExtractor2 ie2 = null;
	
	List<String> clauseTreesInternal = null;
	String currentClause = "";

	@BeforeClass 
	public static void setup() {
		ie2 = new InfoExtractor2();
	}
	
//	@Test
//	public void test0() {
//		Annotation document = new Annotation("I would like to book a table at 7pm for 4 people and i need a high chair because my daugther is 1 year old");
//		ie2.annotateDoc(document);
//		for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
//			Tree t = sentence.get(TreeAnnotation.class);
//			t.pennPrint();
//			// Now, extract any sub-trees (identified by SBAR) and retain
//			ie2.getClauseTrees(t);
//		}
//	}
//	

//	@Test
//	public void test1() {
//		Annotation document = new Annotation("My red mountain bike has a puncture, so i borrowed a bike from Lucy");
//		ie2.annotateDoc(document);
//		for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
//			ie2.extractInfo(sentence, null);
//		}
//	}
//	
//	@Test
//	public void test2() {
//		Annotation document = new Annotation("My credit card has a limit of £3000 and i seem to have exceeded that limit");
//		ie2.annotateDoc(document);
//		for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
//			Tree t = sentence.get(TreeAnnotation.class);
//			//t.pennPrint();
//			// Now, extract any sub-trees (identified by SBAR and CC and IN (Subordinating)) and retain
//			ie2.getClauseTrees(t);
//		}
//	}
//	
//	@Test
//	public void test3() {
//		Annotation document = new Annotation("Boris wanted to go with his friend to Paris on holiday but his passport has expired");
//		ie2.annotateDoc(document);
//		for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
//			Tree t = sentence.get(TreeAnnotation.class);
//			t.pennPrint();
//			// Now, extract any sub-trees (identified by SBAR and CC and IN (Subordinating)) and retain
//			ie2.getClauseTrees(t);
//		}
//	}
	
	@Test
	public void test4() {
		Annotation document = new Annotation("Boris wanted to go on holiday to Paris with his friend but his passport had expired");
		ie2.annotateDoc(document);
		for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
			ie2.extractInfo(sentence, null);
		}
	}
	
//	@Test
//	public void test5() {
//		Annotation document = new Annotation("My name is Geoff and i need a mortgage so I can buy a new house");
//		ie2.annotateDoc(document);
//		for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
//			ie2.extractInfo(sentence, null);
//		}
//	}
	
}
