package uk.me.chrismbell.text_classify.search.ms;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import uk.me.chrismbell.text_classify.ConfigLoader;
import uk.me.chrismbell.text_classify.ConfigurationException;
import uk.me.chrismbell.text_classify.SearchException;
import uk.me.chrismbell.text_classify.StopWords;

public class MicrosoftBingSearchClient {
	
	String subKey = null;
	static Logger logger = LoggerFactory.getLogger(MicrosoftBingSearchClient.class);
	
	public MicrosoftBingSearchClient() throws ConfigurationException {
		subKey = ConfigLoader.getConfig().getMsBingParams().get("subKey");
		
		Unirest.setObjectMapper(new ObjectMapper() {
		    private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
		                = new com.fasterxml.jackson.databind.ObjectMapper();

		    public <T> T readValue(String value, Class<T> valueType) {
		        try {
		            return jacksonObjectMapper.readValue(value, valueType);
		        } catch (IOException e) {
		            throw new RuntimeException(e);
		        }
		    }

		    public String writeValue(Object value) {
		        try {
		            return jacksonObjectMapper.writeValueAsString(value);
		        } catch (JsonProcessingException e) {
		            throw new RuntimeException(e);
		        }
		    }
		});
	}
	
	public BingSearchResultsEnvelope getInfo(String topic) throws SearchException {
		
		HttpResponse<BingSearchResultsEnvelope> response = null;
		try {
			response = Unirest.get("https://api.cognitive.microsoft.com/bing/v5.0/search")
				.header("Accept-Language", "")
				.header("BingAPIs-Market", "en-GB")
				.header("Ocp-Apim-Subscription-Key", subKey)
				.queryString("cc", "GB")
				.queryString("count", "10")
				.queryString("mkt", "en-GB")
				.queryString("responseFilter", "Webpages")
				.queryString("safeSearch", "Strict")
				.queryString("q", topic)
				.asObject(BingSearchResultsEnvelope.class);
	
				return response.getBody();
				
		} catch (UnirestException e) {
			e.printStackTrace();
			throw new SearchException("Could not get results from Google: ",e);
		}
	
	}
	
	public String getTextFromSearchResuts(BingSearchResultsEnvelope res, String searchText, List<String> list) {
		String finalUnstructedTextBlock = "";
		

		for (BingSearchResultsItem sri: res.getWebPages().getValue())
	    {
			logger.debug(sri.getDisplayUrl());
	    	String tweakedString = sri.getSnippet().toLowerCase()
	    				.replaceAll(System.getProperty("line.separator"), "")
	    				.replaceAll(searchText.toLowerCase(),"");
	    	
	    	String tweakedString2 = sri.getName().toLowerCase()
    				.replaceAll(System.getProperty("line.separator"), "")
    				.replaceAll(searchText.toLowerCase(),"");
	    	
	        //documents.add(new Document(sri.link,sri.title,tweakedString ));
	        
	        String newText = StopWords.removeStopWords(tweakedString);
	        String newText2 = StopWords.removeStopWords(tweakedString2);
	        
	        list.add(newText);
	        list.add(newText2);
	        
	        logger.debug((newText + ". "+ newText2));
	        finalUnstructedTextBlock = finalUnstructedTextBlock + ". "+newText+ ". "+newText2;
	        
	    }
		return finalUnstructedTextBlock;
	}

}
