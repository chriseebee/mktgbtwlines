package uk.me.chriseebee.mktgbtwlines2.nlp.dandelion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DandelionSimilarityResult {

//	{
//		  "timestamp": "Date and time of the response generation process",
//		  "time": "Time elapsed for generating the response (milliseconds)",
//		  "lang": "The language used to compare the given texts",
//		  "langConfidence": "Accuracy of the language detection, from 0.0 to 1.0. Present only if auto-detection is on",
//		  "similarity": "Similarity of the two given texts, from 0.0 to 1.0. Higher is better"
//	}
	
	double similarity;

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	
}
