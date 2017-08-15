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
	

}
