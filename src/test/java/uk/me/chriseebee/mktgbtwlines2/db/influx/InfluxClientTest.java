package uk.me.chriseebee.mktgbtwlines2.db.influx;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;

import uk.me.chriseebee.mktgbtwlines2.nlp.InterestingEvent;

public class InfluxClientTest {
 
	@Test
	public void test() {
		InfluxClient ic = new InfluxClient();
		InterestingEvent ev = new InterestingEvent();
		Date d = new Date();
		ev.setDateTime(d.getTime());
		ev.setIdentifiedEntity("Louis Vuitton");
		ev.setSentiment(0.27);
		ev.setIdentifiedEntityType("B");
		
		try {
			ic.sendEventToInflux(ev);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Error");
		}
		
	}
	
	@Test
	public void test2() {
		InfluxClient ic = new InfluxClient();

		try {
			Date d = new Date();
			ic.sendEventToInflux("entity,type=B value=\"Coca-Cola\",sentiment=0.62,intent=\"empty\" "+d.getTime()+"000000");
		
											
		} catch (IOException e) {
			e.printStackTrace();
			fail("Error");
		}
		
	}

}
