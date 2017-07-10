package uk.me.chriseebee.mktgbtwlines2.db;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orientechnologies.orient.core.record.impl.ODocument;

import uk.me.chriseebee.mktgbtwlines2.AvailabilityException;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;
import uk.me.chriseebee.mktgbtwlines2.nlp.InterestingEvent;
import uk.me.chriseebee.mktgbtwlines2.nlp.entity.Entity;

public class OrientClientTest {

	static OrientClient ic = null;
	
	@BeforeClass
	public static void setup() {
		try {
			ic = new OrientClient();
		} catch (ConfigurationException | AvailabilityException e1) {
			e1.printStackTrace();
			fail("Error");
		}
		
		try {
		    ic.putVertex("Person", "Harry Sykes",true);
		    ic.putVertex("Person", "Harry Smith",true);
		} catch (StorageException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCreationOfInterestingEvent() {
		
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
		} catch (StorageException e) {
			e.printStackTrace();
			fail("Error");
		}
		
	}
	
	@Test
	public void testNewDocCreation() {
		ODocument doc = ic.createDocument("Car");
		doc.field( "name", "Fiesta" );
		doc.field( "brand", "Ford" );
		
		ic.saveDocument(doc);
	}
	
	@After
	public void teardown() {
		ic.deleteVertex("Harry Sykes");
		ic.deleteVertex("UNKNOWN");
		ic.deleteVertex("B");
		ic.deleteVertex("Louis Vuitton");
		ic.deleteVertex("Jimmy Jones");
	}

}
