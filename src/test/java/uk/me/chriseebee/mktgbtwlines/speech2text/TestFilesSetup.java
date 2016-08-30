package uk.me.chriseebee.mktgbtwlines.speech2text;

import java.util.ArrayList;
import java.util.List;

public class TestFilesSetup {

	List<String> fileNameList = new ArrayList<String>();
	List<String> trascriptList = new ArrayList<String>();
	
	public TestFilesSetup() {
		
		fileNameList.add("brand_track_001.wav");
		fileNameList.add("brand_track_002.wav");
		fileNameList.add("brand_track_003.wav");
		fileNameList.add("brand_track_004.wav");
		fileNameList.add("brand_track_005.wav");
		fileNameList.add("brand_track_006.wav");
		
		fileNameList.add("m_brand_track_001.wav");
		fileNameList.add("m_brand_track_002.wav");
		fileNameList.add("m_brand_track_003.wav");
		fileNameList.add("m_brand_track_004.wav");
		fileNameList.add("m_brand_track_005.wav");
		fileNameList.add("m_brand_track_006.wav");

		trascriptList.add("Three brands Dove, Axe, and Old Spice have generated tremendous consumer interest and identification in a historically low-involvement category, one you would never expect to get attention on social media.");
		trascriptList.add("The idea that consumers could possibly want to talk about Corona or Coors in the same way that they debate the talents of Ronaldo and Messi is silly.");
		trascriptList.add("I’m a big fan of car manufacturers Audi, as well as BMW and Alfa Romeo.");
		trascriptList.add("What is hard is that Gucci and Luis Vuitton are tricky names to capture");
		trascriptList.add("I do most of my food shopping at Marks & Spencer these days, but in the US Walmart is a huge retailer.");
		trascriptList.add("I’m not sure about Starbucks coffee, or that from Cafe Nero, but Carluccio’s is great.");

	}
	
	
	public List<String> getFileNameList() {
		return fileNameList;
	}
	
	public List<String> getTrascriptList() {
		return trascriptList;
	}
}
