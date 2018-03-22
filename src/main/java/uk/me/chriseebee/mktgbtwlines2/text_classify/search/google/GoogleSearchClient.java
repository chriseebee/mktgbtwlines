package uk.me.chrismbell.text_classify.search.google;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import uk.me.chrismbell.text_classify.AppConfig;
import uk.me.chrismbell.text_classify.ConfigLoader;
import uk.me.chrismbell.text_classify.ConfigurationException;
import uk.me.chrismbell.text_classify.SearchException;
import uk.me.chrismbell.text_classify.StopWords;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

//https://github.com/guillaume-nargeot/n-grams-frequency-java.git
public class GoogleSearchClient { 
	
	private static HttpClient unsafeHttpClient;

	public GoogleSearchClient() {
		
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
	
    static {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            unsafeHttpClient = ((HttpClientBuilder) HttpClients.custom()).setSslcontext(sslContext)
                    .setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public static HttpClient getClient() {
        return unsafeHttpClient;
    }


	public GoogleSearchResults getResults(String searchText) throws SearchException, ConfigurationException {
		
		AppConfig ac = ConfigLoader.getConfig();
		String apiKey = ac.getGoogleParams().get("apiKey");
		String cx = ac.getGoogleParams().get("cx");
		String fields = ac.getGoogleParams().get("fields");
		String numResults = ac.getGoogleParams().get("numResults");
		
//		System.out.println("APIKey = "+ apiKey);
//		System.out.println("CX = "+ cx);
//		System.out.println("Fields = "+ fields);
//		System.out.println("Num Ressults = "+ numResults);
		
		try {
			
            HttpClient creepyClient = GoogleSearchClient.getClient();
            Unirest.setHttpClient(creepyClient);
            
			HttpResponse<GoogleSearchResults> response =	Unirest.get("https://www.googleapis.com/customsearch/v1")
				.queryString("key", apiKey)
				.queryString("fields", fields)
				.queryString("safe", "high")
				.queryString("cr", "countryUK")
				.queryString("cx", cx)
				.queryString("hl", "en")
				.queryString("num", numResults)
				.queryString("q", searchText)
				.asObject(GoogleSearchResults.class);
				
			
			//https://kgsearch.googleapis.com/v1/entities:search?query=Domestic+and+General&key=AIzaSyCPIzcIFmh1VrQOBHJWJVEHCphhqnVEfxA&limit=1&indent=True
				
			return response.getBody();
			
			//System.out.println(jsonResponse.getBody().toString());
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SearchException("Could not get results from Google: ",e);
		}

	}
	
	public String getTextFromSearchResuts(GoogleSearchResults res, String searchText,List<String> list) {
		String finalUnstructedTextBlock = "";
		for (GoogleSearchResultsItem sri : res.getItems())
	    {
	    	String tweakedString = sri.snippet.toLowerCase()
	    				.replaceAll(System.getProperty("line.separator"), "")
	    				.replaceAll(searchText.toLowerCase(),"");
	    	
	    	String tweakedString2 = sri.title.toLowerCase()
    				.replaceAll(System.getProperty("line.separator"), "")
    				.replaceAll(searchText.toLowerCase(),"");
	    	
	        //documents.add(new Document(sri.link,sri.title,tweakedString ));
	        
	        String newText = StopWords.removeStopWords(tweakedString);
	        String newText2 = StopWords.removeStopWords(tweakedString2);
	        
	        list.add(newText);
	        list.add(newText2);
	        
	        //System.out.println((newText));
	        //logger.debug((newText + ". "+ newText2));
	        finalUnstructedTextBlock = finalUnstructedTextBlock + ". "+newText+ ". "+newText2;
	        
	    }
		return finalUnstructedTextBlock;
	}
	
	
	public static void main(String[] args) {
		GoogleSearchClient  sc = new GoogleSearchClient();
		try {
			sc.getResults("DGS/RPP DD");
		} catch (SearchException | ConfigurationException e) {
			e.printStackTrace();
		}
	}
}
