package uk.me.chriseebee.mktgbtwlines2.nlp;

public class InterestingEvent {

	public static final String MALE = "M";
	public static final String FEMALE = "F";
	
	
	private String identifiedEntity;
	private String identifiedEntityType;
	
	//sentence event ( sentiment, intent)
	private double sentiment;
	private String intent;
	private long   dateTime;
	
	public InterestingEvent() { 
		
	}

	public String getIdentifiedEntity() {
		return identifiedEntity;
	}

	public void setIdentifiedEntity(String identifiedEntity) {
		this.identifiedEntity = identifiedEntity;
	}

	public double getSentiment() {
		return sentiment;
	}

	public void setSentiment(double sentiment) {
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
		return "entity,type="+identifiedEntityType+" value=\""+identifiedEntity+"\",sentiment="+sentiment+",intent=\""+intent+"\" "+dateTime+"000000";
	}
 
	public String getIdentifiedEntityType() {
		return identifiedEntityType;
	}

	public void setIdentifiedEntityType(String identifiedEntityType) {
		this.identifiedEntityType = identifiedEntityType;
	}

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}
	
}
