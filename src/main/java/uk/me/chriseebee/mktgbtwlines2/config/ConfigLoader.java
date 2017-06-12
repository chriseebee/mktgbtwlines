package uk.me.chriseebee.mktgbtwlines2.config;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.config.mappers.AppConfig;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ConfigLoader {

	public static final String DEFAULT_CONFIG_FILE = "config.yml";
	static Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
	private static String resourceName = null;
	private static AppConfig config = null;
	
	private static void loadConfig(String fileName)  {
		
		logger.debug("Trying to load config");
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		logger.debug("Mapper created");
		try {
			File f  = new File("./"+DEFAULT_CONFIG_FILE);
			Reader r = new FileReader(f);
			logger.debug("Reader created");
			config = mapper.readValue(r, AppConfig.class);
			logger.debug("Config created");
		} catch (JsonParseException jpe) {
			logger.error("Error loading application configuration",jpe);
		} catch (JsonMappingException jme) {
			logger.error("Error loading application configuration",jme);
		} catch (IOException ioe) {
			logger.error("Error loading application configuration",ioe);
		}
		logger.debug("Config Loaded: "+config.toString());
	}
	
	public static AppConfig getConfig(String fileName) throws ConfigurationException {
		if (config == null) {
			loadConfig(fileName);
			if (config == null) {
				throw new ConfigurationException ("Config is NULL!! Broken!!");
			}
		}
		return config;
	}
	
	public static AppConfig getConfig() throws ConfigurationException {
		if (config == null) {
			loadConfig(DEFAULT_CONFIG_FILE);
			if (config == null) {
				throw new ConfigurationException ("Config is NULL!! Broken!!");
			}
		}
		return config;
	}
}
