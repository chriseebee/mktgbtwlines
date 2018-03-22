package uk.me.chriseebee.audiopitchjava;

import java.util.concurrent.ConcurrentLinkedQueue;

public class LaughterManager {

	// store last 50 pitches
	IntegerRangeFifoQueue q;
	ConcurrentLinkedQueue<String> queue;
	
	int latest = 0;
	
	
	// get the range of pitches in that last n. THe bigger the range, 
	// the more likely laughter as opposed to high frequency chatting
	// male speed = 120hz and male laughter = 282hz
	// female speed = 220hz and female laughter = 421hz
	
	public LaughterManager(ConcurrentLinkedQueue<String> queue) {
		this.queue = queue;
		q = new IntegerRangeFifoQueue(25);
	}
	
	public void addFrequency(int f) {
		if (f<1000) {
			q.add(f);
			latest = f;
		}
	}
	
	// Calculate the laughter likelihood
	//
	// 
	
	// return 0-8
	public int getLaughterQuotient() {
		
		//System.out.print("Latest = "+latest+ ", Average="+(total/(counter+1))+", Range="+q.getRange().toString());
		int tempVal = 0;
		int avg = q.getAverage();
		int diff = (latest-avg);
		if (diff<0) { return 0; }
		
		tempVal = tempVal + (diff/100)*2;
		if (tempVal > 6) { tempVal = 6;}
		tempVal = tempVal + (q.getRange()/300);
		if (tempVal > 8) { tempVal = 8;}
		//System.out.println("::=> "+tempVal);
		
		
		java.util.Date date= new java.util.Date();
		String ts = date.getTime()+"000000";
		String message = "laughcalc value="+tempVal+",range="+q.getRange()+",average="+avg+" "+ts;
		queue.offer(message);
		
		return tempVal;
		
	}
}
