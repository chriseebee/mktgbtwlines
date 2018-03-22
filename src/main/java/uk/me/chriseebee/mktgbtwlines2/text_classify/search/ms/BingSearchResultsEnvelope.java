package uk.me.chrismbell.text_classify.search.ms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class BingSearchResultsEnvelope {
	
	private BingSearchResultsWebpages webPages;

	public BingSearchResultsWebpages getWebPages() {
		return webPages;
	}

	public void setWebPages(BingSearchResultsWebpages webPages) {
		this.webPages = webPages;
	}
	
}
