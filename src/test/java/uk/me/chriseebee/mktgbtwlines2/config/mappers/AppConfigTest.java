package uk.me.chriseebee.mktgbtwlines2.config.mappers;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;

public class AppConfigTest {

	Logger logger = LoggerFactory.getLogger(AppConfigTest.class);

	@Test
	public void test() {
		try {
			AppConfig config =ConfigLoader.getConfig(ConfigLoader.DEFAULT_CONFIG_FILE);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail();
		}
	}
}
