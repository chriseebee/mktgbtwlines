package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import edu.stanford.nlp.ie.util.RelationTriple;

public class SentenceClause {

	private List<RelationTriple> clauseTriples = null;
	
	private List<SentenceClause> dependentClauses = null;
	
	private String clauseString = "";

	public SentenceClause() {
		clauseTriples = new ArrayList<RelationTriple>();
		dependentClauses = new ArrayList<SentenceClause>();
	}
	
	public SentenceClause(String s) {
		clauseTriples = new ArrayList<RelationTriple>();
		dependentClauses = new ArrayList<SentenceClause>();
		clauseString = s;
	}
	
	public void addClause(SentenceClause sc) {
		dependentClauses.add(sc);
	}
	
	public void setClauseString(String s) {
		clauseString = s;
	}
	
	public String getClauseString() {
		return clauseString;
	}
	
	public void appendToClauseString(String s) {
		if (this.dependentClauses.size()>0) {
			this.dependentClauses.get(this.dependentClauses.size()-1).appendToClauseString(s);
		} else {
			clauseString = clauseString + " " + s;
		}
	}
	
	public void print(int index) {
		
		System.out.println(StringUtils.leftPad(" ", index) + StringUtils.trim(clauseString));
		System.out.println("Children = "+dependentClauses.size());
		index = index + 5;
		for (int i=0;i<dependentClauses.size();i++) {
			dependentClauses.get(i).print(index);
		}
	}
	
}
