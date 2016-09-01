package uk.me.chriseebee.mktgbtwlines2.config.mappers;

import java.util.Map;

public class AppConfig {
	
	private Map<String, String> influxParams;
	private Map<String, String> watsonKeys;
	private Map<String, String> witKeys;
	private Map<String, String> audioOptions;
	
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
	
	
	
}
