package uk.me.chriseebee.mktgbtwlines2.nlp.ibm;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;
import uk.me.chriseebee.mktgbtwlines2.config.mappers.AppConfig;

import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentSentiment;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Sentiment;

public class AlchemyClient {

	Logger logger = LoggerFactory.getLogger(AlchemyClient.class);
	private AlchemyLanguage al = null;
			
	public AlchemyClient() throws ConfigurationException {
		AppConfig ac = null; 
		try {
			ConfigLoader cl = ConfigLoader.getConfigLoader();
			ac = cl.getConfig();
			
			if (ac.getWatsonKeys().get("apiKey")!=null) {
				al = new AlchemyLanguage(ac.getWatsonKeys().get("apiKey"));
			} else {
				throw new ConfigurationException ("Error getting API Key for Watson - null");
			}
			
		} catch (ConfigurationException ce) {
			logger.error("Error Setting up Alchemy: "+ce.getMessage(),ce);
			throw ce;		
		} catch (Exception e) {
			logger.error("Error Setting up Alchemy: "+e.getMessage(),e);
			throw new ConfigurationException ("Error Setting up Alchemy");	
		}
	}
	
	public Sentiment getSentenceSentiment(String sentence) {
	    final Map<String, Object> params = new HashMap<String, Object>();
	    params.put(AlchemyLanguage.TEXT, sentence);
	    final DocumentSentiment documentSentiment = al.getSentiment(params).execute();
	    return documentSentiment.getSentiment();
	}
	
	
}
