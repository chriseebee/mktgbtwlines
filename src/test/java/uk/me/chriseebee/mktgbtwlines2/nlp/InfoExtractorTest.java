package uk.me.chriseebee.mktgbtwlines2.nlp;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;


import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.MentionsAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NormalizedNamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;
import semantics.Compare;
import uk.me.chriseebee.mktgbtwlines2.Utils;

public class InfoExtractorTest {

	double confidence = 0.8;
	
	@BeforeClass 
	public static void setup() {
		InfoExtractor.setup();
	}
	
//	@Test
//	public void test1() {
//		InfoExtractor.extractInfoFrom("The Mulberry Bush is a pub, is located in New York and serves Korean food");
//	}
//	
//	@Test
//	public void test1z() {
//		InfoExtractor.extractInfoFrom("The Mulberry Bush is not a pub");
//	}
//	
//	@Test
//	public void test1y() {
//		InfoExtractor.extractInfoFrom("The Mulberry Bush doesn't server Korean food");
//	}
	
//	@Test
//	public void test1b() {
//		InfoExtractor.extractInfoFrom("London based pub; The Mulberry Bush serves Korean food");
//	}
//	
//	@Test
//	public void test1c() {
//		InfoExtractor.extractInfoFrom("The Mulberry Bush is a pub and is located in New York");
//	}
//	
//	@Test
//	public void test2() {
//		InfoExtractor.extractInfoFrom("The Mulberry Bush is situated in london");
//	}
//	
//	@Test
//	public void test3() {
//	    InfoExtractor.extractInfoFrom("Bills is a restaurant and serves Korean food");
//	}
//	
//	@Test
//	public void test4() {
//	    InfoExtractor.extractInfoFrom("DGS/RPP DD is an insurance company");
//	}
	
	
	@Test
	public void test_similarity_1() {
		String a = "ORG is located in LOC";
		String b = "ORG is sitated in LOC";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_2() {
		String a = "ORG is located in LOC";
		String b = "ORG is found in LOC";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_3() {
		String a = "ORG is located in LOC";
		String b = "ORG serves NATIONALITY food";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_4() {
		String a = "ORG is located in LOC";
		String b = "ORG is a type of ORG_TYPE";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_5() {
		String a = "ORG serves NATIONALITY food";
		String b = "ORG is owned by NATIONALITY businessmen";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_6() {
		String a = "ORG serves NATIONALITY food";
		String b = "ORG is owned by NATIONALITY businessmen";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	
	@Test
	public void test_similarity_7() {
		String a = "ORG is based in LOC";
		String b = "ORG is located in LOC";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_8() {
		String a = "LOC based ORG";
		String b = "ORG serves NATIONALITY food";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_9() {
		String a = "LOC based ORG";
		String b = "ORG is based in LOC";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_10() {
		String a = "LOC based ORG";
		String b = "ORG is located in LOC";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_11() {
		String a = "LOC based ORG";
		String b = "NATIONALITY based ORG";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_12() {
		String a = "ORG is known as NOUN_PHRASE";
		String b = "ORG is called NOUN_PHRASE";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_13() {
		String a = "ORG is known as NOUN_PHRASE";
		String b = "ORG is also called NOUN_PHRASE";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_14() {
		String a = "ORG is known as NOUN_PHRASE";
		String b = "ORG is really called NOUN_PHRASE";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_15() {
		String a = "ORG is known as NOUN_PHRASE";
		String b = "ORG is really called LOC";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_16() {
		String a = "ORG is really located at LOC";
		String b = "ORG is really called NOUN_PHRASE";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_17() {
		String a = "ORG is really located at LOC";
		String b = "ORG is really known as NOUN_PHRASE";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
	
	@Test
	public void test_similarity_18() {
		String a = "ORG is NATIONALITY";
		String b = "ORG is a ORG_TYPE";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence));
	}
}
