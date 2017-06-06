package uk.me.chriseebee.mktgbtwlines2.db.influx;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;

import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;
import org.junit.Ignore;
import org.junit.Test;

import uk.me.chriseebee.mktgbtwlines2.db.InfluxClient;
import uk.me.chriseebee.mktgbtwlines2.nlp.InterestingEvent;
import uk.me.chriseebee.mktgbtwlines2.nlp.entity.Entity;

@Ignore
public class InfluxClientTest {
 
	@Test
	public void test() {
		InfluxClient ic =null;
		try {
			ic = new InfluxClient();
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		InterestingEvent ev = new InterestingEvent();
		Date d = new Date();
		ev.setDateTime(d.getTime());
		Entity ent = new Entity();
		ent.setName("Louis Vuitton");
		ent.setType("B");
		ev.setEntity(ent);
		ev.setSentiment(0.27);
		
		try {
			ic.sendEventToDataStore(ev);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Error");
		}
		
	}
	

}
