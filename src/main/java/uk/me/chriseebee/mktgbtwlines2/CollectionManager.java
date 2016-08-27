package uk.me.chriseebee.mktgbtwlines2;

import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.logging.Logger;


public class CollectionManager {
	
	//private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private static CollectionManager _instance = null;
	
	private boolean laughing = false;
	
	private CollectionManager() {}
	
	public static CollectionManager getInstance () {
		if (_instance == null) {
			_instance = new CollectionManager();
		}
		
		return _instance;
	}
	
	//public enum Type { INCREMENT, DECREMENT }
	
	//private ConcurrentHashMap<String,Integer> map = new ConcurrentHashMap<String,Integer>();

  public ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();

	public boolean isLaughing() {
		return laughing;
	}

	public void setLaughing(boolean laughing) {
		this.laughing = laughing;
	}