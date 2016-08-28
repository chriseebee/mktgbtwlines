package uk.me.chriseebee.mktgbtwlines2.config.mappers;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.TaxonomyTests;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;

public class AppConfigTest {

	Logger logger = LoggerFactory.getLogger(TaxonomyTests.class);
	
	@Test
	public void test() {
		
		ConfigLoader cl = null;
		AppConfig ac = null;
		
		try {
			cl = ConfigLoader.getConfigLoader("/config.yml");
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
		
		assertTrue(influxParams.get("hostname").equals("ec2-52-210-133-236.eu-west-1.compute.amazonaws.com"));
		
	}

}
