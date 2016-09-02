package uk.me.chriseebee.mktgbtwlines2.db.influx;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;

import uk.me.chriseebee.mktgbtwlines2.nlp.InterestingEvent;

public class InfluxClientTest {

//	@Test
//	public void test() {
//		InfluxClient ic = new InfluxClient();
//		InterestingEvent ev = new InterestingEvent();
//		Date d = new Date();
//		ev.setDateTime(d.getTime());
//		ev.setIdentifiedEntity("Louis\\ Vuitton");
//		ev.setSentiment("Positive");
//		ev.setIdentifiedEntityType("B");
//		
//		try {
//			ic.sendEventToInflux(ev);
//		} catch (IOException e) {
//			e.printStackTrace();
//			fail("Error");
//		}
//		
//	}
	
	@Test
	public void test2() {
		InfluxClient ic = new InfluxClient();

		// entity,type=B value=Louis\ Vuitton,sex=null,sentiment=Positive,intent=null,adjective=null 1472856193236000000
		
		try {
			ic.sendEventToInflux("nerevent value=BBC 1472856144462000000");
															
		} catch (IOException e) {
			e.printStackTrace();
			fail("Error");
		}
		
	}

}
