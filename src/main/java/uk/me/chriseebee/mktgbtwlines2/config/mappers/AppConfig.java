package uk.me.chriseebee.mktgbtwlines2.config.mappers;

import java.util.Map;

public class AppConfig {
	
	private Map<String, String> influxParams;
	private Map<String, String> orientParams;
	private Map<String, String> watsonKeys;
	private Map<String, String> witKeys;
	private Map<String, String> audioOptions;
	private Map<String, String> audioClipStorageOptions;
	private Map<String, String> dandelionKeys;
	private Map<String, String> s2tParams;
	
	public Map<String, String> getS2tParams() {
		return s2tParams;
	}
	public void setS2tParams(Map<String, String> s2tParams) {
		this.s2tParams = s2tParams;
	}

	public Map<String, String> getDandelionKeys() {
		return dandelionKeys;
	}
	public void setDandelionKeys(Map<String, String> dandelionKeys) {
		this.dandelionKeys = dandelionKeys;
	}
	public Map<String, String> getWatsonKeys() {
		return watsonKeys;
	}
	public void setWatsonKeys(Map<String, String> watsonKeys) {
		this.watsonKeys = watsonKeys;
	}
	public Map<String, String> getWitKeys() {
		return witKeys;
	} 
	
	public void setWitKeys(Map<String, String> witKeys) {
		this.witKeys = witKeys;
	}
	public Map<String, String> getInfluxParams() {
		return influxParams;
	}
	public void setInfluxParams(Map<String, String> influxParams) {
		this.influxParams = influxParams;
	}
	public Map<String, String> getAudioOptions() {
		return audioOptions;
	}
	public void setAudioOptions(Map<String, String> audioOptions) {
		this.audioOptions = audioOptions;
	} 
	
	public void setAudioClipStorageOptions(Map<String, String> audioClipStorageOptions) {
		this.audioClipStorageOptions = audioClipStorageOptions;
	}
	
	public Map<String, String> getAudioClipStorageOptions() {
		return audioClipStorageOptions;
	}
	public Map<String, String> getOrientParams() {
		return orientParams;
	}
	public void setOrientParams(Map<String, String> orientParams) {
		this.orientParams = orientParams;
	}

	
	
	
}
