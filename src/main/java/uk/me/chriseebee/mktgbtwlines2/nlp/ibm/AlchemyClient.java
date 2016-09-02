package uk.me.chriseebee.mktgbtwlines2.nlp.ibm;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.mappers.AppConfig;

import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentSentiment;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Sentiment;

public class AlchemyClient {

	private AlchemyLanguage al = null;
			
	public AlchemyClient() {
		AppConfig ac = null; 
		try {
			ConfigLoader cl = ConfigLoader.getConfigLoader();
			ac = cl.getConfig();
		} catch (Exception e) {
			e.printStackTrace();
			fail("BIG ERROR LOADING CONFIG");
			return;
		}
		al = new AlchemyLanguage(ac.getWatsonKeys().get("apiKey"));
		
	}
	
	public Sentiment getSentenceSentiment(String sentence) {
	    final Map<String, Object> params = new HashMap<String, Object>();
	    params.put(AlchemyLanguage.TEXT, sentence);
	    final DocumentSentiment documentSentiment = al.getSentiment(params).execute();
	    return documentSentiment.getSentiment();
	}
	
	
}
