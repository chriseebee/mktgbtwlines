package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.RelationsResult;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.CoreAnnotations.MentionsAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import semantics.Compare;
import uk.me.chriseebee.mktgbtwlines.speech2text.ibm.WatsonClient;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;
import uk.me.chriseebee.mktgbtwlines2.nlp.dandelion.DandelionClient;
import uk.me.chriseebee.mktgbtwlines2.nlp.entity.stanford.StanfordClient;

public class InfoExtractor {

	public static final String WATSON_ANNOTATOR = "WATSON";
	public static final String DANDELION_ANNOTATOR  =  "DANDELION";
	public static final String STANFORD_ANNOTATOR   = "STANFORD";
	
	public static final String ENTITY_ANNOTATION = "ENTITY";
	public static final String CATEGORY_ANNOTATION = "CATEGORY";
	public static final String CONCEPT_ANNOTATION = "CONCEPT";
	public static final String KEYWORD_ANNOTATION = "KEYWORD";
	public static final String RELATION_ANNOTATION = "RELATION";
	
	static Logger logger = LoggerFactory.getLogger(InfoExtractor.class);
	
	static void extractInfoFrom(String text, String annotators, String annotations) {
		
		List<String> annotatorsList = Arrays.asList(annotators.split(","));
		List<String> annotationsList = Arrays.asList(annotations.split(","));
		
		logger.info("---------------------");
		logger.info(text);
		logger.info("---------------------");
		
		AnalysisResults combinedResults = new AnalysisResults();
		
		if (annotatorsList.contains(WATSON_ANNOTATOR)) {
			logger.info(" - Running Watson");
			// Setup Watson Client
			try {
				WatsonClient.setup();
			} catch (ConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		    // first go and get info from Watson for this document
		    AnalysisResults watsonResults = WatsonClient.process(text,annotationsList);	
		    mergeResults(combinedResults,watsonResults);
		}
		
		if (annotatorsList.contains(DANDELION_ANNOTATOR)) {
			logger.info(" - Running Dandelion");
			try {
				DandelionClient.setup();
			} catch (ConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		    // first go and get info from Watson for this document
		    AnalysisResults dandelionResults = WatsonClient.process(text,annotationsList);	
		    mergeResults(combinedResults,dandelionResults);
		}

		if (annotatorsList.contains(STANFORD_ANNOTATOR)) {
			logger.info(" - Running Stanford");
			StanfordClient.setup();
			AnalysisResults stanfordResults = StanfordClient.process(text);
			mergeResults(combinedResults,stanfordResults);
		}
		

		WatsonClient.printConfidentResults(combinedResults);
	}
	
	private static AnalysisResults mergeResults (AnalysisResults master, AnalysisResults child) {
		
		if (child.getCategories()!=null) {
			List<CategoriesResult> cats = master.getCategories();
			if (cats==null) {
				cats = new ArrayList<CategoriesResult>();
			}
			for (CategoriesResult cr : child.getCategories()) {
				cats.add(cr);
				master.setCategories(cats);
			}
		}
		
		if (child.getEntities()!=null) {
			List<EntitiesResult> ents = master.getEntities();
			if (ents==null) {
				ents= new ArrayList<EntitiesResult>();
			}
			for (EntitiesResult er: child.getEntities()) {
				ents.add(er);
				master.setEntities(ents);
			}
		}
		
		if (child.getConcepts()!=null) {
			for (ConceptsResult cor: child.getConcepts()) {
				master.addconcepts(cor);
			}
		}
		
		if (child.getKeywords()!=null) {
			for (KeywordsResult kr: child.getKeywords()) {
				master.addkeywords(kr);
			}
		}
		
		if (child.getRelations()!=null) {
			for (RelationsResult rr: child.getRelations()) {
				master.addrelations(rr);
			}
		}
		
		return master;
		
	}
	

	
//	public String normaliseRelation(String foundRelation) {	
//		
//		private static void normaliseRelations() {
//	        // What is the subject? If it is not a location or organisation, question the user and override
//	        
//	        if (entities.get(triple.subjectGloss().toUpperCase()) == null) {
//	        	// lets go hunting in the words to make sure it's a set of nouns
//	        	
//	        	if (isNounPhrase(triple.subject)) {
//	        		logger.info("Subject = NOUN_PHRASE");
//	        	}
//	        	// if so, then lets look it up in our other 
//	        } else {
//	        	logger.info("Subject = " + entities.get(triple.subjectGloss().toUpperCase()));
//	        }
//	        
//	        
//	        // What is the object
//	        if (entities.get(triple.objectGloss().toUpperCase()) == null) {
//	        	// lets go hunting in the words to make sure it's a set of nouns
//	        	if (isNounPhrase(triple.object)) {
//	        		logger.info("Object = NOUN_PHRASE");
//	        	}
//	        	// if so, then lets look it up in our other 
//	        } else {
//	        	logger.info("Object = " + entities.get(triple.subjectGloss().toUpperCase()));
//	        }
//	        
//
//	      	}
//		}
//
//	    String b = "ORG is found at LOC";
//	
//	    Compare c = new Compare(foundRelation,b);
//	    logger.info("Similarity between the sentences\n-"+foundRelation+"\n-"+b+"\n is: " + c.getResult());
//	    return "X";
//	}
//	
//	public static boolean areStringsSemanticallySimilar(String a, String b, double confidenceLimit ) {		
//
//	    Compare c = new Compare(a,b);
//	    return (c.getResult()>=confidenceLimit);
//
//	}
	
}
