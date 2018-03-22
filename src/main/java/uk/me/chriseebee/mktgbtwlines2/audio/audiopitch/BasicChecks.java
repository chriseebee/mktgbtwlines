package uk.me.chriseebee.audiopitchjava;

import java.util.Date;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

public class BasicChecks {

	 public static void main(String[] args) {
		
		 //Date d = new Date();
		 //System.out.println(d.getTime());
		 AudioUtils au = new AudioUtils();
		 au.printMixers();
		 
		 Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
         Mixer mixer = AudioSystem.getMixer(mixerInfo[2]);
		 au.printMixerSupport(mixer);
		 
	 }
}
