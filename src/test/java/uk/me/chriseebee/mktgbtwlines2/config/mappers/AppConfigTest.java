package uk.me.chriseebee.mktgbtwlines2.config.mappers;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;

public class AppConfigTest {

	Logger logger = LoggerFactory.getLogger(AppConfigTest.class);
	
	@Test
	public void test() {
		
		ConfigLoader cl = null;
		AppConfig ac = null;
		
		try {
			cl = ConfigLoader.getConfigLoader("config.yml");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Major error in finding resource");
		}
		
		try {
			ac =cl.getConfig();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Major error in loading config");
		}
		
		Map<String,String> influxParams = ac.getInfluxParams();
		logger.info("Hostname = "+influxParams.get("hostname"));
		assertTrue(!influxParams.get("hostname").equals("XXX"));
		
	}

}
