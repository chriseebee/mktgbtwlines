package uk.me.chriseebee.mktgbtwlines2.nlp;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NLUComparisonTester {

	Logger logger = LoggerFactory.getLogger(NLUComparisonTester.class);
	
	private String annotators = "WATSON,DANDELION,STANFORD";
	private String annotations = "RELATION,CONCEPT,ENTITY,CATEGORY,KEYWORD";

	@Test
	public void test1() {

//		InfoExtractor.extractInfoFrom("Applegreen runs a Fuel station/services", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Cash Genie is a payday lender", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Bet 365 is an online gambling company", annotators,annotations);
//		InfoExtractor.extractInfoFrom("CHB stands for Child Benefit", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Domestic & General provides warranties for domestic appliances", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Equifax is one of the UK's leading credit reporting companies", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Grattan is an online catalogue retailer", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Home Retail Group is a leading home and general merchandise retailer, with brands such as Argos and Homebase", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Hutchison 3G UK is the company name for Three Mobile", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Kingfisher is the company group name for B&Q and Screwfix", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Lowell Group provides businesses with debt recovery services", annotators,annotations);
//		InfoExtractor.extractInfoFrom("MBNA is a Credit card provider", annotators,annotations);
		InfoExtractor.extractInfoFrom("McAfee provides anti-virus and internet safety software", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Payday Express is the trading name of Instant Cash Loans Ltd", annotators,annotations);
//		InfoExtractor.extractInfoFrom("TfL is Transport for London", annotators,annotations);
//		InfoExtractor.extractInfoFrom("18118 Beauty is a beauty club subscription service providing discounts on treatments.", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Adrian Flux Insurance is a specialist motor vehicle insurance provider.", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Foxlow is a restaurant in Chiswick, London", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Petter Pharmacy is located in Muswell Hill", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Memsaab is an Indian restaurant located in Leeds", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Scribbler is a stationary supplier located in Waterloo Railway Station", annotators,annotations);
//		InfoExtractor.extractInfoFrom("The Ogilvy is a pub located in Woking", annotators,annotations);
//		InfoExtractor.extractInfoFrom("Memsaab is an Indian restaurant and is located in Leeds", annotators,annotations);


	}
	

	
}
