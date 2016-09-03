package uk.me.chriseebee.mktgbtwlines2.config;


import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.config.mappers.AppConfig;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ConfigLoader {

	private static final String DEFAULT_CONFIG_FILE = "/config.yml";
	Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
	String resourceName = null;
	AppConfig config;
	
	private static ConfigLoader _singleton = null;
	
	public static ConfigLoader getConfigLoader() throws Exception {
		if (_singleton==null) {
			_singleton = new ConfigLoader(DEFAULT_CONFIG_FILE);
			_singleton.loadConfig();
		}
		
		return _singleton;
	}
	
	public static ConfigLoader getConfigLoader(String fileName) throws Exception {
		if (_singleton==null) {
			_singleton = new ConfigLoader(fileName);
			_singleton.loadConfig();
		}
		
		return _singleton; 
	}
	
	
	private ConfigLoader(String fileName) throws Exception {
		
        try {
        	resourceName = ((fileName!=null) ? fileName : DEFAULT_CONFIG_FILE);
        } catch (Exception e) {
        	logger.error("Could not load config file",e);
        	throw e;
        }
	}
	
	private void loadConfig() throws Exception {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			Reader r = new InputStreamReader(ConfigLoader.class.getResourceAsStream(resourceName));
			config = mapper.readValue(r, AppConfig.class);
		} catch (JsonParseException e) {
			logger.error("Error loading application configuration",e);
			throw new Exception ("Configuration Load Error");
		} catch (JsonMappingException e) {
			logger.error("Error loading application configuration",e);
			throw new Exception ("Configuration Load Error");
		} catch (IOException e) {
			logger.error("Error loading application configuration",e);
			throw new Exception ("Configuration Load Error");
		}
	}
	
	public AppConfig getConfig() {
		return config;
	}
}
