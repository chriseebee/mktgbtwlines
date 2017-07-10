package uk.me.chriseebee.mktgbtwlines2.nlp.dandelion;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;

public class DandelionClientTest {

	@BeforeClass
	public static void setup() {
		try {
			DandelionClient.setup();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void test() {
		DandelionClient.query("Arcadia is a big company");
	}

}
