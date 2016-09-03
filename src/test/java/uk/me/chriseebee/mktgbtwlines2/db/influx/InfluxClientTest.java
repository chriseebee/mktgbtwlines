package uk.me.chriseebee.mktgbtwlines2.db.influx;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;

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
			Date d = new Date();
			//ic.sendEventToInflux("nerevent value=BBC "+d.getTime()+"000000");
			//ic.sendEventToInflux("measurement value=12 "+d.getTime()+"000000");
			ic.sendEventToInflux("entity,type=P value=food "+d.getTime()+"000000");
											
		} catch (IOException e) {
			e.printStackTrace();
			fail("Error");
		}
		
	}

}
