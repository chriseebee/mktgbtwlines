package uk.me.chriseebee.mktgbtwlines2.nlp.entity.stanford;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsResult;
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
import edu.stanford.nlp.naturalli.OpenIE;
import edu.stanford.nlp.naturalli.SentenceFragment;
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
	static OpenIE openie = null;;

	public static void setup() {
		tlp = new PennTreebankLanguagePack();
		gsf = tlp.grammaticalStructureFactory();
		openie = new OpenIE();
	}

	public static Tree getTree(CoreMap sentence) {

			Tree t = sentence.get(TreeAnnotation.class);
			return t;
	}
	
	public static List<CoreMap> getAnnotatedSentences(String text) {
		// Setup Stanford NLP Pipeline
		Annotation doc = new Annotation(text);
		try {
			NLPPipeline.getPipeline().annotate(doc);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc.get(CoreAnnotations.SentencesAnnotation.class);
	}
	
	public static List<SentenceFragment> getAnnotatedSentenceClauses(CoreMap sentence) {
		return openie.clausesInSentence(sentence);
	}
	
	public static List<SentenceFragment> getAnnotatedEntailments(SentenceFragment clause) {
		return openie.entailmentsFromClause(clause);
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

		List<EntitiesResult> entities2 = new ArrayList<EntitiesResult>();
		List<RelationsResult> relations = new ArrayList<RelationsResult>();

		// Loop over sentences in the document
		for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {

			Tree t = sentence.get(TreeAnnotation.class);
			
			StanfordClient.getMainVerb(t);

			List<CoreMap> mentions = sentence.get(MentionsAnnotation.class);

			Map<String, String> entities = mentions.stream()
					.collect(Collectors.toMap(token -> token.get(TextAnnotation.class).toUpperCase(),
							token -> token.get(NamedEntityTagAnnotation.class)));

			for (String e : entities.keySet()) {
				logger.debug("TOKEN/ENTITY = " + e + "/" + entities.get(e));
				EntitiesResult er = new EntitiesResult();
				er.setText(e);
				er.setType(entities.get(e));
				er.setRelevance(1.0);
				entities2.add(er);
			}

			// Get the OpenIE triples for the sentence
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			// Print the triples
			for (RelationTriple triple : triples) {

				// TODO: Here we need to strip out useless junk from the relationship to focus
				// on the main verb and find out the tense
				

				RelationsResult rr = new RelationsResult();
				// Subject
				RelationEntity re_subj = new RelationEntity();
				re_subj.setText(triple.subjectLemmaGloss());
				if (isNounPhrase(triple.subject)) {
					re_subj.setType("NOUN_PHRASE");
				}
				RelationArgument ra_subj = new RelationArgument();
				ra_subj.setEntities(new ArrayList<RelationEntity>(Arrays.asList(re_subj)));
				ra_subj.setText("SUBJECT");

				// Object
				RelationEntity re_obj = new RelationEntity();
				re_obj.setText(triple.objectLemmaGloss());
				if (isNounPhrase(triple.object)) {
					re_obj.setType("NOUN_PHRASE");
				}
				RelationArgument ra_obj = new RelationArgument();
				ra_obj.setEntities(new ArrayList<RelationEntity>(Arrays.asList(re_obj)));
				ra_obj.setText("OBJECT");

				// Finish
				rr.setArguments(new ArrayList<RelationArgument>(Arrays.asList(ra_subj, ra_obj)));
				rr.setScore(triple.confidence);

				String type = "";
				// rr.setType(triple.relationLemmaGloss());

				// Is this relationship a negative one?
				if (t != null) {
					if (isNegation(t, new IndexedWord(triple.relationHead()))) {
						logger.debug("THIS IS A NEGATIVE SENTENCE");
						// We are hacking this field for both positive/negative as well as source
						type = "[NEGATION]";
					}
				} else {
					logger.debug("Tree is null");
				}

				rr.setType(type + triple.relationLemmaGloss());
				relations.add(rr);
			}
		}

		ar.setRelations(relations);
		ar.setEntities(entities2);

		return ar;

	}

	private static boolean isNounPhrase(List<CoreLabel> words) {
		return !words.stream().filter(cl -> !cl.get(PartOfSpeechAnnotation.class).startsWith("NN")).findFirst()
				.isPresent();
	}

	private static String getMainVerb(Tree parseTree) {

		// For each Tree
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parseTree);

		Collection<TypedDependency> tdl = gs.typedDependencies();

		Optional<TypedDependency> foundRoot = tdl.stream().filter(td -> td.reln().toPrettyString().equals("root"))
				.findFirst();

		if (foundRoot.isPresent()) {
			logger.debug("OK, we've found the Root node: "+foundRoot.toString());

			
			return foundRoot.toString();
		} else {
			return null;
		}

	}
	
	private static boolean isNegation(Tree parseTree,IndexedWord relationHead) {
	

		// For each Tree
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parseTree);

		Collection<TypedDependency> tdl = gs.typedDependencies();

		Optional<TypedDependency> foundNegation = tdl.stream().filter(td -> td.reln().toPrettyString().equals("neg"))
				.findFirst();

		if (foundNegation.isPresent()) {
			logger.debug("OK, we've found a negation, but it is related to the head word of the relation in question?");

			GrammaticalRelation gr = gs.getGrammaticalRelation(foundNegation.get().gov(), relationHead);
			logger.debug(gr.toPrettyString());
			return true;
		} else {
			return false;
		}

	}

}
