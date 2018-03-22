package uk.me.chrismbell.text_classify.nlu.razor;

import com.textrazor.AnalysisException;
import com.textrazor.NetworkException;
import com.textrazor.TextRazor;
import com.textrazor.annotations.Entity;
import com.textrazor.annotations.Topic;

import uk.me.chrismbell.text_classify.AppConfig;
import uk.me.chrismbell.text_classify.ConfigLoader;
import uk.me.chrismbell.text_classify.ConfigurationException;

import com.textrazor.annotations.AnalyzedText;

public class TextRazorClient {
	
	String apiKey = null;

	public TextRazorClient() throws ConfigurationException {
		AppConfig ac = ConfigLoader.getConfig();
		apiKey = ac.getTextRazorParams().get("apiKey");

	}
	
	public void analyzeText(String text) {
		TextRazor client = new TextRazor(apiKey);
	
		client.addExtractor("topics");
		client.addExtractor("entities");
	
		AnalyzedText response = null;
		try {
			response = client.analyze(text);
		} catch (NetworkException | AnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		for (Entity entity : response.getResponse().getEntities()) {
		    System.out.println("Matched Entity: " + entity.getEntityId() + "/" + entity.getConfidenceScore());
		}
		
		for (Topic topic: response.getResponse().getTopics()) {
		    System.out.println("Matched Topic: " + topic.getLabel() + "/" + topic.getScore());
		}
	}
}



