package uk.me.chriseebee.mktgbtwlines2.nlp.dandelion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DandelionResultsItem {
	
//	{
//	    "types": ["http://dbpedia.org/ontology/Film", "http://dbpedia.org/ontology/Wikidata:Q11424", "http://dbpedia.org/ontology/Work"],
//	    "spot": "Scribbler",
//	    "confidence": 0.7385,
//	    "start": 0,
//	    "alternateLabels": ["The Scribbler"],
//	    "end": 9,
//	    "id": 40126795,
//	    "label": "The Scribbler",
//	    "categories": ["2014 films", "2010s thriller films", "American films", "American thriller films", "English-language films", "Dissociative identity disorder in fiction", "Films shot in Los Angeles, California", "Films based on British comics"],
//	    "title": "The Scribbler (film)",
//	    "uri": "http://en.wikipedia.org/wiki/The_Scribbler_%28film%29"
//	  }

	private String[] types;
	private String spot;
	private Double confidence;
	private String[] categories;
	private String title;
	private String label;
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String[] getTypes() {
		return types;
	}
	public void setTypes(String[] types) {
		this.types = types;
	}
	public String getSpot() {
		return spot;
	}
	public void setSpot(String spot) {
		this.spot = spot;
	}
	public Double getConfidence() {
		return confidence;
	}
	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}
	public String[] getCategories() {
		return categories;
	}
	public void setCategories(String[] categories) {
		this.categories = categories;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
}
