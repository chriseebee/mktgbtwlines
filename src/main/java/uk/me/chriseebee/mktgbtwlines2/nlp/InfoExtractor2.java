package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class InfoExtractor2 {
	
	static Logger logger = LoggerFactory.getLogger(InfoExtractor2.class);

	static StanfordCoreNLP pipeline = null;

	public InfoExtractor2() {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,parse,truecase,pos,lemma");
		pipeline = new StanfordCoreNLP(props);
	}
	
	public void annotateDoc(Annotation document) {
		pipeline.annotate(document);
	}
	
	public SentenceContext extractInfo(CoreMap sentence, String additionalContext) { // note the param will need to change
		Tree t = sentence.get(TreeAnnotation.class);
		SentenceContext sCon = new SentenceContext();
		t.pennPrint();
		getClauseTreesInner(t,sCon, null);
		
		return sCon;
	}
	
	private SentenceClause getClauseTreesInner(Tree t, SentenceContext sCon, SentenceClause sc) {
		
		SentenceClause newChildClause  = null;
		
		if (sc ==null) { sc = new SentenceClause(); }
		
		int i = 0;
		for (Tree child : t.getChildrenAsList()) {
			i++;
			logger.debug(child.toString() + " - " + i);
			
			if (child.label().value().matches("CC|IN") && !child.firstChild().value().equals("of")) {
				logger.debug("Clause Boundary");
				if (sc.getClauseString().length()>0) { 
					logger.debug("Creating new sub-clause as parent is not empty");
					newChildClause = new SentenceClause();
					sc.addClause(newChildClause);
				} else {
					logger.debug("Not creating new sub-clause as parent is empty");
					newChildClause = sc;
				}
				if (child.label().value().equals("IN")) {
					newChildClause.setClauseString( "DEP:");
				} 
			} else {
				//logger.debug("- ");
				if (child.isLeaf()) {
					
					if (newChildClause!=null) {
						newChildClause.appendToClauseString(child.value());
						logger.debug(newChildClause.getClauseString());
					} else {
						sc.appendToClauseString(child.value());
						logger.debug(sc.getClauseString());
					}
				}
			}
			
			if (child.numChildren()>0) {
				
				if (newChildClause!=null) {
					logger.debug("Calling Inner with new");
					getClauseTreesInner(child, sCon, newChildClause);
				} else {
					logger.debug("Calling Inner with sc");
					getClauseTreesInner(child, sCon, sc);
				}
				
			} 
		}
		
		if (t.label().value().equals("ROOT")) {
			logger.debug("Back at Root");
			if (sc.getClauseString().length()>0) { 
				sc.print(0);
				sCon.addClause(sc); 
			}

		}
		
		return newChildClause==null ? sc : newChildClause;
		
	}
	
	
}
