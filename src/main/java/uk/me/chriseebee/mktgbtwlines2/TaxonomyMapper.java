package uk.me.chriseebee.mktgbtwlines2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.util.MultidimensionalCounter.Iterator;

public class TaxonomyMapper {

	List<String> categories =  Collections.synchronizedList(new ArrayList<String>());
	
	public TaxonomyMapper() {
		
	}
	
	private void addString(String text) {
		String text2 = text.trim().replace("&amp;","&").replace("\\","");
		if (!categories.contains(text2)) {
			categories.add(text2);
		}		
	}
	
	public void mapFile() {
		try {
			BufferedReader fr = new BufferedReader(new FileReader("/Users/cbell/Desktop/MTP/cats.txt"));
			
			String line;
			try {
				line = fr.readLine();
				
				while (line!=null) {
					
					if (!line.startsWith("<") && !line.startsWith("}")) {
					
						System.out.println(line);
						String product;
						//4993 - Animals &amp; Pet Supplies &gt; Pet Supplies &gt; Bird Supplies &gt; Bird Treats
						
						if (line.contains("&gt;")) {
							String[] sections= line.split("&gt;");
							String[] firstSections = sections[0].split("-");
							
							addString(firstSections[1]);
							for (int i=1;i<sections.length;i++) {
								//System.out.println("Adding: "+sections[i].trim());
								addString(sections[i]);
							}
						} else {
							String [] firstSections = line.split("-");
							addString(firstSections[1]);
						}
						
					}
					
					line = fr.readLine();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Length = "+categories.size());
		java.util.Iterator<String> iter = categories.iterator();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/cbell/Documents/java_workspace/mktgbtwlines2/resources/ner/train2.data.txt"));
			
			while (iter.hasNext()) {
		
			//bw.write(iter.next() + "\tPRODUCT\n");
				bw.write(iter.next() + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
	
	
	public void mapFileToWords() {
		try {
			BufferedReader fr = new BufferedReader(new FileReader("/Users/cbell/Documents/java_workspace/mktgbtwlines2/resources/ner/brands3.txt"));
			
			String line;
			try {
				line = fr.readLine();
				
				while (line!=null) {
					
					if (!line.startsWith("<") && !line.startsWith("}")) {
							String[] sections= line.split(" ");
							
							for (int i=1;i<sections.length;i++) {
								
								String temp = sections[i];
								temp = temp.replace("\\", "");
								if (temp.endsWith("ies")) { 
									temp = temp.substring(0,temp.length()-3);
									temp = temp + "y";
								}
								if (temp.endsWith("s")) { 
									temp = temp.substring(0,temp.length()-1);
								}
								System.out.println("Adding: "+sections[i]+":"+temp);
								addString(temp);
							}

						
					}
					
					line = fr.readLine();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		System.out.println("Length = "+categories.size());
		java.util.Iterator<String> iter = categories.iterator();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/cbell/Documents/java_workspace/mktgbtwlines2/resources/ner/brands3.txt.toc"));
			
			while (iter.hasNext()) {
		
			bw.write(iter.next() + "\tBRAND\n");
			}
			
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
	
	
	public void removeDuplicates() {
		try {
			BufferedReader fr = new BufferedReader(new FileReader("/Users/cbell/Documents/java_workspace/mktgbtwlines2/resources/ner/brands3.txt.tok"));
			
			String line;
			try {
				line = fr.readLine();
				
				while (line!=null) {
					addString(line);
					
					line = fr.readLine();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Length = "+categories.size());
		java.util.Iterator<String> iter = categories.iterator();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/cbell/Documents/java_workspace/mktgbtwlines2/resources/ner/brands.txt.tok2"));
			
			while (iter.hasNext()) {
		
			bw.write(iter.next() + "\tBRAND\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
	
	
	public static void main (String[] args) {
		TaxonomyMapper tm = new TaxonomyMapper();
		tm.mapFile();
		//m.removeDuplicates();
	}
}
