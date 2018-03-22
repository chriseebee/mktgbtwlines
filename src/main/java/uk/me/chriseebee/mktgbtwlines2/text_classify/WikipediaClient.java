package uk.me.chrismbell.text_classify;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class WikipediaClient {

	public WikipediaClient() {
		// TODO Auto-generated constructor stub
	}
	
	public void getInfo(String topic) {
		
		try {
			Unirest.get("https://en.wikipedia.org/w/api.php")
				.queryString("action", "query")
				.queryString("titles", topic)
				.queryString("prop", "categories")
				.queryString("format", "json")
				.asJson();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
