package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.util.Random;

import com.orientechnologies.orient.core.record.impl.ODocument;

import uk.me.chriseebee.mktgbtwlines2.nlp.entity.Entity;

public class InterestingEvent {
	
	private static final String DEFAULT_ACTOR="UNKNOWN";

	public static final String MALE = "M";
	public static final String FEMALE = "F";
	
	private String actorName;
	
	private Entity e = null;
	private int	entityCountInUtterance = 0;
	
	public int getEntityCountInUtterance() {
		return entityCountInUtterance;
	}


	public void setEntityCountInUtterance(int entityCountInUtterance) {
		this.entityCountInUtterance = entityCountInUtterance;
	}

	//sentence event ( sentiment, intent)
	private Double sentiment;
	private String intent;
	private long   dateTime;
	 
	public InterestingEvent() { 
		
	}
	
	
	public String getActorName() {
		return actorName != null ? actorName : DEFAULT_ACTOR;
	}

	public void setActorName(String actorName) {
		this.actorName = actorName;
	}


	public Entity getEntity() {
		return e;
	}

	public void setEntity(Entity entity) {
		this.e= entity;
	}

	public Double getSentiment() {
		return sentiment;
	}

	public void setSentiment(Double sentiment) {
		this.sentiment = sentiment;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}
	
	/**
	 * overrides the default toString with the format required for Influx
	 */
	public String toString() {
		int rand = (new Random()).nextInt(900000) + 100000;
		return "entity,type="+e.getType()+" value=\""+e.getName()+"\",sentiment="+sentiment+",intent=\""+intent+"\" "+dateTime+""+rand;
	}


	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}
	
}
