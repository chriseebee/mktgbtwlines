package uk.me.chriseebee.mktgbtwlines2.nlp.entity;

public class Entity {
	
	public static final String SOURCE_WATSON = "WATSON_NLU";
	public static final String VALIDITY_UNPROVEN = "UNPROVEN";
	public static final String VALIDITY_AFFIRMED = "AFFIRMED";

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	private String name;
	private String type;
	private String topCategory;
	private String source;
	private String validity;
	
	public Entity() {
	}
	
	public Entity(String name) {
		this.name = name;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return topCategory;
	}

	public void addCategory(String category) {
		this.topCategory = category;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
