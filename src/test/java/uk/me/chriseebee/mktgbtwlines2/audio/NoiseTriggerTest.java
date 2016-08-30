package uk.me.chriseebee.mktgbtwlines2.audio;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.me.chriseebee.mktgbtwlines2.audio.NoiseTrigger;

public class NoiseTriggerTest {

	@Test
	public void test() {
        NoiseTrigger nt = new NoiseTrigger();
	    Thread t2 = new Thread (nt);
	    t2.start();
	    try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    nt.stopRunning();
		//fail("Not yet implemented");
	}

}
