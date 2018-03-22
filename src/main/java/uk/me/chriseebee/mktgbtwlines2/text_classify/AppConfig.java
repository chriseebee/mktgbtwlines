package uk.me.chrismbell.text_classify;

import java.util.Map;

public class AppConfig {
	
	private Map<String, String> googleParams;
	private Map<String, String> watsonKeys;
	private Map<String, String> textRazorParams;
	private Map<String, String> msBingParams;
	public Map<String, String> getMsBingParams() {
		return msBingParams;
	}

	public void setMsBingParams(Map<String, String> msBingParams) {
		this.msBingParams = msBingParams;
	}

	private Map<String, String> runOptions;

	public Map<String, String> getRunOptions() {
		return runOptions;
	}

	public void setRunOptions(Map<String, String> runOptions) {
		this.runOptions = runOptions;
	}

	public Map<String, String> getTextRazorParams() {
		return textRazorParams;
	}

	public void setTextRazorParams(Map<String, String> textRazorParams) {
		this.textRazorParams = textRazorParams;
	}

	public Map<String, String> getGoogleParams() {
		return googleParams;
	}

	public void setGoogleParams(Map<String, String> googleParams) {
		this.googleParams = googleParams;
	}

	public void setWatsonKeys(Map<String, String> watsonKeys) {
		this.watsonKeys = watsonKeys;
	}

	public Map<String, String> getWatsonKeys() {
		return watsonKeys;
	}
}
