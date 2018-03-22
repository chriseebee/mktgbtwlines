package uk.me.chrismbell.text_classify.nlu.watson;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.RelationsOptions;

import uk.me.chrismbell.text_classify.AppConfig;
import uk.me.chrismbell.text_classify.ConfigLoader;
import uk.me.chrismbell.text_classify.ConfigurationException;


public class WatsonClient {

	Logger logger = LoggerFactory.getLogger(WatsonClient.class);
	
	private String nluUsername = null;
	private String nluPassword = null;
	
	public WatsonClient() throws ConfigurationException {
		
		AppConfig ac = ConfigLoader.getConfig();
		nluUsername = ac.getWatsonKeys().get("nluUsername");
		nluPassword = ac.getWatsonKeys().get("nluPassword");
	}
	
	public AnalysisResults process(String text) {
		
		NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
				  NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
				  nluUsername,
				  nluPassword
				);

		CategoriesOptions categories = new CategoriesOptions();
		
		ConceptsOptions.Builder cb = new ConceptsOptions.Builder();
		cb.limit(new Integer(10));
		ConceptsOptions conceptOptions = cb.build();
		
		KeywordsOptions.Builder b = new KeywordsOptions.Builder();
		b.limit(new Integer(10));
		KeywordsOptions keywordoptions = b.build();
		
		EntitiesOptions entitiesOptions = new EntitiesOptions.Builder()
				  .emotion(false)
				  .sentiment(true)
				  .limit(10)
				  .build();
		

		RelationsOptions.Builder rb= new RelationsOptions.Builder();
		RelationsOptions relationsOptions = rb.build();

		Features features = new Features.Builder()
		  .categories(categories)
		  .concepts(conceptOptions)
		  .keywords(keywordoptions)
		  .entities(entitiesOptions)
		  .relations(relationsOptions)
		  .build();

		AnalyzeOptions parameters = new AnalyzeOptions.Builder()
		  .text(text)
		  .features(features)
		  .build();

		AnalysisResults response = service
		  .analyze(parameters)
		  .execute();
		
		
		//System.out.println(response);
		
		return response;
	}

	public void printConfidentResults(AnalysisResults ar) {
		
		List<ConceptsResult> concepts = ar.getConcepts();
	
		for (ConceptsResult cr : concepts) {
			if (cr.getRelevance()>0.7) {
				System.out.println("CONCEPT: "+cr.getText() + "/" + cr.getRelevance());
			}
		}
		
		List<KeywordsResult> keywords = ar.getKeywords();
		
		for (KeywordsResult kr : keywords) {
			if (kr.getRelevance()>0.7) {
				System.out.println("KEYWORD: "+kr.getText() + "/" + kr.getRelevance());
			}
		}
		
		List<CategoriesResult> categories = ar.getCategories();
		
		for (CategoriesResult catr : categories) {
			if (catr.getScore()>0.01) {
				System.out.println("CATEGORY: "+catr.getLabel() + "/" + catr.getScore());
			}
		}
		
		
		List<EntitiesResult> entities = ar.getEntities();
		
		for (EntitiesResult e : entities) {
			if (e.getRelevance()>0.05) {
				System.out.println("ENTITY: "+e.getText() + "/" + e.getType() + "/" + e.getRelevance());
			}
		}
		
	}
	
}


