package uk.me.chrismbell.text_classify.search.web;

import java.io.IOException;
import java.util.Arrays;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class WebPageInfoExtractor {

	public static void getWordsFromWebPage(String url, String searchText) {
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			Elements items = doc.select(":contains("+searchText+")");
			
			for (Element item : items) {
				if( Arrays.asList("h1","h2","p","div").contains(item.tagName())) {
				
					//if (item.childNodes().size()==0) {
						System.out.println(item.id() + "/" + item.childNodeSize() 	+ "/" + item.tagName() + ": " + item.text());
					//}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return "";
	}
	
	public static void main (String[] args) {
		WebPageInfoExtractor.getWordsFromWebPage("http://actprogramme.org.uk/dgsrpp-dd-contact-number/", "DGS/RPP DD");
	}
}
