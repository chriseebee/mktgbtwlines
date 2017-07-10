package uk.me.chriseebee.mktgbtwlines2.nlp.entity.stanford;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.RelationArgument;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.RelationEntity;
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
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import uk.me.chriseebee.mktgbtwlines2.nlp.NLPPipeline;

public class StanfordClient {

	static Logger logger = LoggerFactory.getLogger(StanfordClient.class);
	
	static TreebankLanguagePack tlp = null;
	// Uncomment the following line to obtain original Stanford Dependencies 
	// tlp.setGenerateOriginalDependencies(true); 
	static GrammaticalStructureFactory gsf = null;
	
	public static void setup() {
		tlp = new PennTreebankLanguagePack();
		gsf = tlp.grammaticalStructureFactory();
	}
	
	public static AnalysisResults process(String text) {
	 
		AnalysisResults ar = new AnalysisResults();
		// Setup Stanford NLP Pipeline
		Annotation doc = new Annotation(text);
	    try {
	    	NLPPipeline.getPipeline().annotate(doc);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }


	    // Loop over sentences in the document
	    for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
    	
	    	Tree t = sentence.get(TreeAnnotation.class);

	    	List<CoreMap> mentions = sentence.get(MentionsAnnotation.class);
	    
	    	//sentence.get(key)
	    
	    	Map<String, String> entities = mentions.stream()
	    		.collect(
	    				Collectors.toMap(
	    						token -> token.get(TextAnnotation.class).toUpperCase(), token -> token.get(NamedEntityTagAnnotation.class)
	    						)
	    				);

		    for (String e: entities.keySet()) {
		    	logger.info("TOKEN/ENTITY = " + e + "/" + entities.get(e));
		    	EntitiesResult er = new EntitiesResult();
		    	er.setText(e);
		    	er.setType(entities.get(e));
		    	er.setRelevance(1.0);
		    	try {
		    		ar.addentities(er);
		    	} catch (Exception ex) {
		    		// assuming this would be thrown in case of dup
		    		logger.debug("Duplicate Entity found in list");
		    	}
		    }
	    
	      // Get the OpenIE triples for the sentence
	      Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
	      // Print the triples
	      for (RelationTriple triple : triples) {
	    	  
	    	  RelationsResult rr = new RelationsResult();
	    	  
	    	  // Subject
	    	  RelationEntity re_subj = new RelationEntity();
	    	  re_subj.setText(triple.subjectGloss());
	    	  if (isNounPhrase(triple.subject)) {
	    		  re_subj.setType("NOUN_PHRASE");
	    	  }
	    	  RelationArgument ra_subj = new RelationArgument();
	    	  ra_subj.addentities(re_subj);
	    	  ra_subj.setText("SUBJECT");
	    	  
	    	  // Relation
	    	  RelationEntity re_rel = new RelationEntity();
	    	  re_rel.setText(triple.relationGloss());
	
	    	  RelationArgument ra_rel= new RelationArgument();
	    	  ra_rel.addentities(re_rel);
	    	  ra_rel.setText("RELATION");
	    	  
	    	  //Object
	    	  RelationEntity re_obj = new RelationEntity();
	    	  re_obj.setText(triple.objectGloss());
	    	  if (isNounPhrase(triple.object)) {
	    		  re_obj.setType("NOUN_PHRASE");
	    	  }
	    	  RelationArgument ra_obj = new RelationArgument();
	    	  ra_obj.addentities(re_obj); 
	    	  ra_obj.setText("OBJECT");
	    	  
	    	  // Finish
	    	  rr.addarguments(ra_subj);
	    	  rr.addarguments(ra_rel);
	    	  rr.addarguments(ra_obj);
	    	  rr.setScore(triple.confidence);
	    	  
	//        System.out.println(triple.confidence + "\t" +
	//            triple.subjectGloss() + "\t" +
	//            triple.relationGloss() + "\t" +
	//            triple.objectGloss() + "\t" +
	//            triple.confidence
	//        	);
	    	  
		      // Is this relationship a negative one?
		    	if (t != null) {
			    	if (isNegation(t,new IndexedWord(triple.relationHead()))) {
			    		logger.info("THIS IS A NEGATIVE SENTENCE");
			    		rr.setType("NEGATIVE");
			    	} else {
			    		rr.setType("POSITIVE");
			    	}
		    	} else {
		    		logger.info("Tree is null");
		    		
		    	}
	        
	    	  ar.addrelations(rr);
	
	      }
	    }
	    
      logger.info("+++++++++++++++++++++");
      
      return ar;

    }

	private static boolean isNounPhrase(List<CoreLabel> words) {
		return !words.stream().filter(cl -> !cl.get(PartOfSpeechAnnotation.class).startsWith("NN")).findFirst()
				.isPresent();
	}
	
	private static boolean isNegation(Tree parseTree,IndexedWord relationHead) {

		// For each Tree
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parseTree); 
		
		Collection<TypedDependency> tdl = gs.typedDependencies();
			
		Optional<TypedDependency> foundNegation = tdl.stream()
					.filter(td -> td.reln().toPrettyString().equals("neg"))
					.findFirst();
		
		if (foundNegation.isPresent()) {
			logger.info("OK, we've found a negation, but it is related to the head word of the relation in question?");

			GrammaticalRelation gr = gs.getGrammaticalRelation(foundNegation.get().gov(), relationHead);
			logger.info(gr.toPrettyString());
			return true;
		} else {
			return false;
		}

	}

}
