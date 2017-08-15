package uk.me.chriseebee.mktgbtwlines2.nlp;

import static org.junit.Assert.*;
import semantics.Compare;

import org.junit.Test;

public class SemanticSimilarityTest {

	double confidence = 0.8;
	String annotators = "DANDELION";
	
	@Test
	public void test_similarity_1() {
		String a = "ORG is located in LOC";
		String b = "ORG is sitated in LOC";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_2() {
		String a = "ORG is located in LOC";
		String b = "ORG is found in LOC";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_3() {
		String a = "ORG is located in LOC";
		String b = "ORG serves NATIONALITY food";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_4() {
		String a = "ORG is located in LOC";
		String b = "ORG is a type of ORG_TYPE";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_5() {
		String a = "ORG serves NATIONALITY food";
		String b = "ORG is owned by NATIONALITY businessmen";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_6() {
		String a = "ORG serves NATIONALITY food";
		String b = "ORG is owned by NATIONALITY businessmen";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	
	@Test
	public void test_similarity_7() {
		String a = "ORG is based in LOC";
		String b = "ORG is located in LOC";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_8() {
		String a = "LOC based ORG";
		String b = "ORG serves NATIONALITY food";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_9() {
		String a = "LOC based ORG";
		String b = "ORG is based in LOC";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_10() {
		String a = "LOC based ORG";
		String b = "ORG is located in LOC";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_11() {
		String a = "LOC based ORG";
		String b = "NATIONALITY based ORG";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_12() {
		String a = "ORG is known as NOUN_PHRASE";
		String b = "ORG is called NOUN_PHRASE";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_13() {
		String a = "ORG is known as NOUN_PHRASE";
		String b = "ORG is also called NOUN_PHRASE";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_14() {
		String a = "ORG is known as NOUN_PHRASE";
		String b = "ORG is really called NOUN_PHRASE";
		assertTrue("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_15() {
		String a = "ORG is known as NOUN_PHRASE";
		String b = "ORG is really called LOC";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_16() {
		String a = "ORG is really located at LOC";
		String b = "ORG is really called NOUN_PHRASE";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_17() {
		String a = "ORG is really located at LOC";
		String b = "ORG is really known as NOUN_PHRASE";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	@Test
	public void test_similarity_18() {
		String a = "ORG is NATIONALITY";
		String b = "ORG is a ORG_TYPE";
		assertFalse("Phrases are similar",InfoExtractor.areStringsSemanticallySimilar(a,b,confidence,annotators ));
	}
	
	
}
