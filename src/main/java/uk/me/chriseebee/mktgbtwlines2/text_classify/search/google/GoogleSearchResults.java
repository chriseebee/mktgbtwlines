package uk.me.chrismbell.text_classify.search.google;

import java.util.ArrayList;
import java.util.List;

public class GoogleSearchResults {

//	{"kind":"customsearch#search",
//	"items":[{"snippet":"To get support from Domestic & General with your DGS/RPP DD bank statement \nentries, contact them on the phone by dialling 0800 561 4493. You can also ..."
//,"link":"http://actprogramme.org.uk/dgsrpp-dd-contact-number/"
//			,"title":"DGS/RPP DD Contact Number: 0800 561 4493 \u2013 ACT Directory"},{"snippet":"","link":"http://www.domesticandgeneral.com/content/contact-domestic-general","title":"Domestic & General Insurance Plc"},{"snippet":"Ever seen BACS, DD or SO on your bank statements and wondered what it \nmeans? ... Domestic & General Services, DGS/RPP, Domestic & General \nServices ...","link":"http://www.moneysavingexpert.com/banking/bank-statement-codes","title":"Bank statement codes: what do they mean? \u2013 MoneySavingExpert"},{"snippet":"The UK's leading specialist warranty provider. Kitchen appliances, boilers and \nconsumer electronics.","link":"http://www.domesticandgeneral.com/","title":"Domestic & General"},{"snippet":"Provides extended warranty quotes, protection and advice for domestic \nappliances and central heating systems.","link":"http://www.domgen.com/termsconditions.html","title":"Warranty - Terms & Conditions of our Extended Warranty - Domestic ..."},{"snippet":"We've teamed up with Domestic & General, the leading specialist warranty \nprovider in the UK, to offer you the AO Aftercare Protection Plan. Read more at ...","link":"http://ao.com/help-and-advice/help-with-my-product/aftercare","title":"AO Aftercare | Protection Plan | Aftercare | ao.com"},{"snippet":"Use our quick and easy number plate search function to search over 56 million \nprivate number plates for your name or initials. We are open 7 days. Test us on ...","link":"https://www.nationalnumbers.co.uk/search.htm","title":"Number Plates Search | National Numbers"},{"snippet":"Currys Customer Services - Find out about Delivery & Installation our Returns \nPolicy. Contact Us Here. FAQs.","link":"http://www.currys.co.uk/gbuk/customer-services-1143-theme.html","title":"Currys Customer Service Delivery, Installation, Contact Us| Currys"},{"snippet":"experiments exclusively dGs were incorporated opposite C-EtT residues, which \n.... Dawid, I.B., Brown, D.D., Reeder, R.H. (1970) J. Mol. Biol. 51, 341-360. 30.","link":"https://academic.oup.com/nar/article-pdf/18/14/4131/7077919/18-14-4131.pdf","title":"Use of shuttle vectors to study the molecular processing of defined ..."},{"snippet":"did not occur more frequently at the cw-DDP modified dGs than ..... 9 Lasko,D.D., \nHarvey,S.C, Malaikal,S.B., Kadlubar. ... M. and Fuchs,R.P.P. (1987) Proc. Natl.","link":"https://academic.oup.com/nar/article-pdf/23/20/4066/7068478/23-20-4066.pdf","title":"Mutagenic and genotoxic effects of DNA adducts formed by the ..."}]}

 
	private String kind;
	private List<GoogleSearchResultsItem> items = new ArrayList<GoogleSearchResultsItem>();
 
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public List<GoogleSearchResultsItem> getItems() {
		return items;
	}
	
	public void setItems(List<GoogleSearchResultsItem> items) {
		this.items = items;
	} 

}
