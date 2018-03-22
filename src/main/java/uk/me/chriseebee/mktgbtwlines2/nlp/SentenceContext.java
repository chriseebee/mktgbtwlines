package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.util.ArrayList;
import java.util.List;

public class SentenceContext {

	private List<SentenceClause> topLevelClauses = null;

	
	public SentenceContext() {
		topLevelClauses = new ArrayList<SentenceClause>();	
	}
	
	public void addClause(SentenceClause sc) {
		topLevelClauses.add(sc);
	}
	

}
