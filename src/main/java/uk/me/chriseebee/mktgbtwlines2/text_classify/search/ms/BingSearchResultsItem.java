package uk.me.chrismbell.text_classify.search.ms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class BingSearchResultsItem {

		private String displayUrl;
		private String snippet;
		private String name;
		
		public String getDisplayUrl() {
			return displayUrl;
		}
		public void setDisplayUrl(String displayUrl) {
			this.displayUrl = displayUrl;
		}
		public String getSnippet() {
			return snippet;
		}
		public void setSnippet(String snippet) {
			this.snippet = snippet;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
}
