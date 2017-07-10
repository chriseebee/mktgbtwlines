package uk.me.chriseebee.mktgbtwlines2.nlp.dandelion;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesResult;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;
import uk.me.chriseebee.mktgbtwlines2.config.mappers.AppConfig;


public class DandelionClient {
	
	private static Map<String, String> entityMapping = Collections.synchronizedMap(new HashMap<String,String>());
	

	static Logger logger = LoggerFactory.getLogger(DandelionClient.class);
	
	private static String apiKey = null;

	
	public static void setup() throws ConfigurationException {
		if (apiKey==null) {
			AppConfig cl = ConfigLoader.getConfig();
			apiKey = cl.getDandelionKeys().get("apiKey");
		}
		
		entityMapping.put("http://dbpedia.org/ontology/Location","LOCATION");
		entityMapping.put("http://dbpedia.org/ontology/Organization","ORGANIZATION");

	
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
	
	public static AnalysisResults query(String text) {
		
		AnalysisResults ar = new AnalysisResults();
		HttpResponse<DandelionResults> output = null;
		try {
			output = Unirest.get("https://api.dandelion.eu/datatxt/nex/v1/")
				.queryString("text", text)
				.queryString("include", "types,categories,alternate_labels")
				.queryString("lang", "en")
				.queryString("country", "GB")
				.queryString("token", apiKey)
				.queryString("epsilon", 0.4)
				.asObject(DandelionResults.class);
			
			
			
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (DandelionResultsItem item: output.getBody().getAnnotations()) {
			List<String> types = Arrays.asList(item.getTypes());
			
			for (String t: types) {
				
				if (t == null) {
					// Mark this as a Concept
					ConceptsResult cr = new ConceptsResult();
					cr.setRelevance(item.getConfidence());
					cr.setText(item.getLabel());
				} else if (entityMapping.containsKey(t)) {
					EntitiesResult er = new EntitiesResult();
					er.setType(entityMapping.get(t));
					er.setText(item.getLabel());
					er.setRelevance(item.getConfidence());
					ar.addentities(er);
				} else {
					
				}
			}

		}
		
		return ar;
	}
}
