package uk.me.chriseebee.mktgbtwlines2.nlp.dandelion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.RelationArgument;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.RelationEntity;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.RelationsResult;
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
	
	private static ObjectMapper om = null;

	static Logger logger = LoggerFactory.getLogger(DandelionClient.class);
	
	private static String apiKey = null;

	
	public static void setup() throws ConfigurationException {
		if (apiKey==null) {
			AppConfig cl = ConfigLoader.getConfig();
			apiKey = cl.getDandelionKeys().get("apiKey");
		}
		
		entityMapping.put("http://dbpedia.org/ontology/Location","Location");
		entityMapping.put("http://dbpedia.org/ontology/Organisation","Organization");

		om = new ObjectMapper() {
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
			    	jacksonObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			        return jacksonObjectMapper.writeValueAsString(value);
			    } catch (JsonProcessingException e) {
			        throw new RuntimeException(e);
			    }
			}
		};
		
		
		Unirest.setObjectMapper(om);
	}
	
	public static AnalysisResults process(String text, List<String> annotationList) {
		
		logger.debug("Calling Dandelion NLU");
		
		AnalysisResults ar = new AnalysisResults();
		HttpResponse<DandelionResults> output = null;
		
		
		try {
			
			long startTime = System.currentTimeMillis();
			
			output = Unirest.get("https://api.dandelion.eu/datatxt/nex/v1/")
				.queryString("text", text)
				.queryString("include", "types,categories,alternate_labels,lod")
				.queryString("lang", "en")
				.queryString("country", "GB")
				.queryString("token", apiKey)
				//.queryString("epsilon", 0.4)
				.queryString("min_confidence", 0.0)
				.asObject(DandelionResults.class);
			
			logger.info("Execution Time = "+(System.currentTimeMillis()-startTime));
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<EntitiesResult> entities = new ArrayList<EntitiesResult>();
		List<ConceptsResult> concepts = new ArrayList<ConceptsResult>();
		List<RelationsResult> relations = new ArrayList<RelationsResult>();
		
		for (DandelionResultsItem item: output.getBody().getAnnotations()) {
			
			logger.debug(" - Processing an item");
			
			logger.debug(om.writeValue(item));
			
			List<String> types = Arrays.asList(item.getTypes());
			
			logger.debug("  -- Types = "+types.size());
			if (types.size()==0) {
				logger.debug("  -- Processing a Concept");
				// Mark this as a Concept
				ConceptsResult cr = new ConceptsResult();
				cr.setRelevance(item.getConfidence());
				cr.setText(item.getLabel());
				concepts.add(cr);
			}  
				
			for (String t: types) {
				
				if (entityMapping.containsKey(t)) {
					logger.debug("  -- Processing an Entity");
					EntitiesResult er = new EntitiesResult();
					er.setType(entityMapping.get(t));
					er.setText(item.getSpot());
					er.setRelevance(item.getConfidence());
					entities.add(er);
					
					if (!item.getSpot().equals(item.getTitle())) {
						// we could create a known as relation here
						RelationsResult rr = new RelationsResult();
				    	  // Subject
				    	  RelationEntity re_subj = new RelationEntity();
				    	  re_subj.setText(item.getSpot());
				    	  RelationArgument ra_subj = new RelationArgument();
				    	  ra_subj.setEntities(new ArrayList<RelationEntity>(Arrays.asList(re_subj)));
				    	  ra_subj.setText("SUBJECT");
				    	  
				    	  //Object
				    	  RelationEntity re_obj = new RelationEntity();
				    	  re_obj.setText(item.getTitle());
				    	  RelationArgument ra_obj = new RelationArgument();
				    	  ra_obj.setEntities(new ArrayList<RelationEntity>(Arrays.asList(re_obj)));
				    	  ra_obj.setText("OBJECT");
				    	  
				    	  rr.setType("is known as");
				    	  
				    	  // Finish
				    	  rr.setArguments(new ArrayList<RelationArgument>(Arrays.asList(ra_subj,ra_obj)));
				    	  rr.setScore(item.getConfidence());
				    	  
				    	  relations.add(rr);
					}
				} else {
					logger.debug("  -- Processing something else - TODO");
				}
			}
		}
		
		ar.setEntities(entities);
		ar.setConcepts(concepts);
		ar.setRelations(relations);
		
		ar.setAnalyzedText("DANDELION");
		
		logger.info("Calling Dandelion NLU: FINISHED");
		
		return ar;
	}
	
	public static double getSimilarity(String a, String b) throws Exception {

		logger.debug("Calling Dandelion NLU");
		
		HttpResponse<DandelionSimilarityResult> output = null;
		try {
			output = Unirest.get("https://api.dandelion.eu/datatxt/sim/v1")
				.queryString("text1", a)
				.queryString("text2", b)
				.queryString("lang", "en")
				.queryString("token", apiKey)
				.asObject(DandelionSimilarityResult.class);
			
			return output.getBody().getSimilarity();
			
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
}
