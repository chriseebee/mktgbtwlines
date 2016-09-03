package uk.me.chriseebee.mktgbtwlines2.nlp;

public class InterestingEvent {

	public static final String MALE = "M";
	public static final String FEMALE = "F";
	
	
	private String identifiedEntity;
	private String identifiedEntityType;
	
	//sentence event ( sex, sentiment, intent, adjective)
	private String sex;
	private String sentiment;
	private String intent;
	private String adjective;
	private long   dateTime;
	
	public InterestingEvent() { 
		
	}

	public String getIdentifiedEntity() {
		return identifiedEntity;
	}

	public void setIdentifiedEntity(String identifiedEntity) {
		this.identifiedEntity = identifiedEntity;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSentiment() {
		return sentiment;
	}

	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public String getAdjective() {
		return adjective;
	}

	public void setAdjective(String adjective) {
		this.adjective = adjective;
	}
	
	/**
	 * overrides the default toString with the format required for Influx
	 */
	public String toString() {
		return "entity,type="+identifiedEntityType+" name="+identifiedEntity+",sex="+sex+",sentiment="+sentiment+",intent="+intent+",adjective="+adjective+" "+dateTime+"000000";
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
