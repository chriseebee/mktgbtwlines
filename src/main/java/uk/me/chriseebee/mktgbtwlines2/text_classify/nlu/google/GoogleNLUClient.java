package uk.me.chrismbell.text_classify.nlu.google;

import java.io.IOException;

import com.google.cloud.language.spi.v1.LanguageServiceClient;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.Sentiment;

public class GoogleNLUClient {

	  public void analyzeText(String text) throws IOException {
	    // Instantiates a client
	    LanguageServiceClient language = LanguageServiceClient.create();

	    // The text to analyze
	    Document doc = Document.newBuilder()
	            .setContent(text).setType(Type.PLAIN_TEXT).build();

	    // Detects the sentiment of the text
	    Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

	    AnalyzeEntitiesResponse aer = language.analyzeEntities(doc,EncodingType.UTF8);
	    System.out.printf("Text: %s%n", text);
	    
	    System.out.println("Entities");
	    
	    for (Entity e: aer.getEntitiesList()){
	    System.out.printf(" - Entity: %s with confidence = %s%n", e.getName(),e.getSalience());
	    }
	    System.out.printf("Sentiment: %s, %s%n", sentiment.getScore(), sentiment.getMagnitude());
	  }
}
