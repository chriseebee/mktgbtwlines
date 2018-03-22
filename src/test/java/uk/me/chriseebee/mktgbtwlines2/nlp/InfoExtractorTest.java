package uk.me.chriseebee.mktgbtwlines2.nlp;

import static org.junit.Assert.*;



import org.junit.BeforeClass;
import org.junit.Test;


public class InfoExtractorTest {
	
	@Test
	public void test1() {
		InfoExtractor.extractInfoFrom("Boris wanted to go on holiday","STANFORD","ENTITY,RELATION");
	}
	
//	@Test
//	public void test1() {
//		InfoExtractor.extractInfoFrom("My name is Geoff and i need a mortgage so I can buy a new house","STANFORD","ENTITY,RELATION");
//	}
//	
//	@Test
//	public void test2() {
//		InfoExtractor.extractInfoFrom("I am 47 years old and i am splitting up from my wife","STANFORD","ENTITY,RELATION");
//	}
	
//	
//	@Test
//	public void test2() {
//		InfoExtractor.extractInfoFrom("Alpha Snapper sits next to Table Noodle","STANFORD","ENTITY,RELATION");
//	}
//	
//	@Test
//	public void test3() {
//		InfoExtractor.extractInfoFrom("The lady called Table Noodle is sitting next to the man known as Alpha Snapper","STANFORD","ENTITY,RELATION");
//	}
//	
//	@Test
//	public void test4() {
//		InfoExtractor.extractInfoFrom("The lady is called Table Noodle","STANFORD","ENTITY,RELATION");
//	}
	
//	@Test
//	public void test5() {
//		InfoExtractor.extractInfoFrom("My name is Table Noodle","STANFORD","ENTITY,RELATION");
//	}
//	
//	@Test
//	public void test5a() {
//		InfoExtractor.extractInfoFrom("Her name is Table Noodle","STANFORD","ENTITY,RELATION");
//	}
//	
//	@Test
//	public void test5b() {
//		InfoExtractor.extractInfoFrom("He is called Table Noodle","STANFORD","ENTITY,RELATION");
//	}
//	
//	@Test
//	public void test6() {
//		InfoExtractor.extractInfoFrom("Chris Bell went to Manchester on holiday","STANFORD","ENTITY,RELATION");
//	}
//	
//	@Test
//	public void test6a() {
//		InfoExtractor.extractInfoFrom("Chris Bell went to Manchester on holiday with Michelle","STANFORD","ENTITY,RELATION");
//	}
//	
//	@Test
//	public void test7() {
//		InfoExtractor.extractInfoFrom("My car has a steering wheel","STANFORD","ENTITY,RELATION");
//	}
//	
//	@Test
//	public void test8() {
//		InfoExtractor.extractInfoFrom("The car has a steering wheel","STANFORD","ENTITY,RELATION");
//	}
//	
//	@Test
//	public void test9() {
//		InfoExtractor.extractInfoFrom("My credit card has a limit of £3000","STANFORD","ENTITY,RELATION");
//	}
//	@Test
//	public void test9a() {
//		InfoExtractor.extractInfoFrom("i seem to have exceeded that limit","STANFORD","ENTITY,RELATION");
//	}
	
	
	
//	@Test
//	public void test1z() {
//		InfoExtractor.extractInfoFrom("The Mulberry Bush is not a pub");
//	}
//	
//	@Test
//	public void test1y() {
//		InfoExtractor.extractInfoFrom("The Mulberry Bush doesn't server Korean food");
//	}
	
//	@Test
//	public void test1b() {
//		InfoExtractor.extractInfoFrom("London based pub; The Mulberry Bush serves Korean food");
//	}
//	
//	@Test
//	public void test1c() {
//		InfoExtractor.extractInfoFrom("The Mulberry Bush is a pub and is located in New York");
//	}
//	
//	@Test
//	public void test2() {
//		InfoExtractor.extractInfoFrom("The Mulberry Bush is situated in london");
//	}
//	
//	@Test
//	public void test3() {
//	    InfoExtractor.extractInfoFrom("Bills is a restaurant and serves Korean food");
//	}
//	
//	@Test
//	public void test4() {
//	    InfoExtractor.extractInfoFrom("DGS/RPP DD is an insurance company");
//	}
	

}
