package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.collectingAndThen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.RelationsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SemanticRolesResult;

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
		
		logger.debug("---------------------");
		logger.debug(text);
		
		AnalysisResults combinedResults = new AnalysisResults();
		combinedResults.setAnalyzedText(text);
		
		if (annotatorsList.contains(WATSON_ANNOTATOR)) {
			logger.debug(" - Running Watson");
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
			logger.debug(" - Running Dandelion");
			try {
				DandelionClient.setup();
			} catch (ConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		    // first go and get info from Watson for this document
		    AnalysisResults dandelionResults = DandelionClient.process(text,annotationsList);	
		    mergeResults(combinedResults,dandelionResults);
		}

		if (annotatorsList.contains(STANFORD_ANNOTATOR)) {
			logger.debug(" - Running Stanford");
			StanfordClient.setup();
			AnalysisResults stanfordResults = StanfordClient.process(text);
			mergeResults(combinedResults,stanfordResults);
		}
		

		WatsonClient.printConfidentResults(combinedResults);
	}
	
	private static AnalysisResults mergeResults (AnalysisResults master, AnalysisResults child) {
		
		List<CategoriesResult> cats = master.getCategories();
		if (cats==null) {
			cats = new ArrayList<CategoriesResult>();
		}
		if (child.getCategories()!=null) {
			for (CategoriesResult cr : child.getCategories()) {
				cats.add(cr);
			}
		}
		master.setCategories(cats);
		
		List<EntitiesResult> ents = master.getEntities();
		if (ents==null) {
			ents= new ArrayList<EntitiesResult>();
		}
		if (child.getEntities()!=null) {
			for (EntitiesResult er: child.getEntities()) {
				ents.add(er);
			}
			
		}
		master.setEntities(ents);
		
		List<ConceptsResult> cons = master.getConcepts();
		if (cons==null) {
			cons= new ArrayList<ConceptsResult>();
		}
		if (child.getConcepts()!=null) {
			for (ConceptsResult cor: child.getConcepts()) {
				cons.add(cor);
			}
		}
		master.setConcepts(cons);
		
		List<KeywordsResult> kews = master.getKeywords();
		if (kews==null) {
			kews= new ArrayList<KeywordsResult>();
		}
		if (child.getKeywords()!=null) {
			for (KeywordsResult kr: child.getKeywords()) {
				kews.add(kr);
			}
		}
		master.setKeywords(kews);
		
		List<RelationsResult> rels = master.getRelations();
		if (rels==null) {
			rels= new ArrayList<RelationsResult>();
		}
		if (child.getRelations()!=null) {
			for (RelationsResult rr: child.getRelations()) {
				rels.add(rr);
			}
		}
		master.setRelations(rels);
		
		List<SemanticRolesResult> sro = master.getSemanticRoles();
		if (sro==null) {
			sro= new ArrayList<SemanticRolesResult>();
		}
		if (child.getSemanticRoles()!=null) {
			for (SemanticRolesResult srr: child.getSemanticRoles()) {
				sro.add(srr);
			}
		}
		master.setSemanticRoles(sro);
		
		return master;
		
	}
	
	public AnalysisResults normaliseResults(AnalysisResults results) {
		// Rules
		
		// 1. Remove duplicate entities, but we need to up the confidence of a repeat I think before we remove
		
		Comparator<EntitiesResult> byAllEntityItems = (e1, e2) -> (e1.getText()+":"+e1.getType()).compareToIgnoreCase(e2.getText()+";"+e2.getType());
		List<EntitiesResult> uniqueEntities = results.getEntities().stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(byAllEntityItems)),
                                           ArrayList::new));
		results.setEntities(uniqueEntities);
		
		// 2. 
		
		// 3. Remove relations where a part of an entity name is referred to in the subject/object
	
	
		// 4. Turn the relations into patterns, normalise them against a list of known patterns, thus removing the crud
		
		// Concern: How to prune relations without losing the right level of item
		return results;
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
	public static boolean areStringsSemanticallySimilar(String a, String b, double confidenceLimit, String annotators ) {		

		List<String> annotatorsList = Arrays.asList(annotators.split(","));
		
		double localResult = -1;
		
		if (annotatorsList.contains("LOCAL")) {
			Compare c = new Compare(a,b);
			localResult = c.getResult();
		}
		
		double dandelionResult = -1;
		
		if (annotatorsList.contains("DANDELION")) {
			
			try {
				DandelionClient.setup();
				dandelionResult = DandelionClient.getSimilarity(a, b);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
		}
		
		double combinedResult = 0;
		// HACK ALERT
	    if (localResult > 0 && dandelionResult >0 && annotatorsList.size()==2) {
	    	combinedResult =(localResult + dandelionResult)/2;
	    } else {
	    	if (annotatorsList.get(0).equals("DANDELION")) { 
	    		combinedResult = dandelionResult; 
	    	}
	    	if (annotatorsList.get(0).equals("LOCAL")) { 
	    		combinedResult = localResult; 
	    	}
	    }

	    return (combinedResult>=confidenceLimit);
	}
	
}
