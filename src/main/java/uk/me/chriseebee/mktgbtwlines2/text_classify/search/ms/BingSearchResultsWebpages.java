package uk.me.chrismbell.text_classify.search.ms;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class BingSearchResultsWebpages {

	private List<BingSearchResultsItem> value = new ArrayList<BingSearchResultsItem>();

	public List<BingSearchResultsItem> getValue() {
		return value;
	}

	public void setValue(List<BingSearchResultsItem> value) {
		this.value = value;
	}
	
	
}
