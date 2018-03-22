package uk.me.chriseebee.audiopitchjava;

import java.sql.Timestamp;
import java.util.Date;

public class InfluxGrafanaTester {
	
	CollectionManager cm;
	int counter = 0;

	public InfluxGrafanaTester() throws InterruptedException {
        Thread qc = new Thread (new QueueConsumer("127.0.0.1"));
        qc.start();
        cm = CollectionManager.getInstance();

        
        while (counter<101) {
            java.util.Date date= new java.util.Date();
			String message = String.format("pitch,laughing=1 value="+Math.random()+" "+date.getTime()+"000000");
			cm.queue.offer(message);
			counter++;
			Thread.sleep(500);
        }
	}
	
	 public static void main(String[] args) {
		 try {
			InfluxGrafanaTester igt = new InfluxGrafanaTester();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     Thread qc = new Thread (new QueueConsumer("127.0.0.1"));
	     qc.start();
		 
	 }
	
}
