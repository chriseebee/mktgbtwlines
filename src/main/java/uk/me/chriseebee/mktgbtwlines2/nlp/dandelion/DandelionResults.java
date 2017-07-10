package uk.me.chriseebee.mktgbtwlines2.nlp.dandelion;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DandelionResults {

	private List<DandelionResultsItem> annotations = new ArrayList<DandelionResultsItem>();

	public List<DandelionResultsItem> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<DandelionResultsItem> annotations) {
		this.annotations = annotations;
	}
		
}
