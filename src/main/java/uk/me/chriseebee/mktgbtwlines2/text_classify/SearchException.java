package uk.me.chrismbell.text_classify;

import com.mashape.unirest.http.exceptions.UnirestException;

public class SearchException extends Exception {

	public SearchException(String string, UnirestException e) {
		super (string,e);
	}

}
