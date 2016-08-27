package uk.me.chriseebee.mktgbtwlines2;

import java.util.Queue;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public class AudioBuffer {

	Queue<TimedAudioBuffer> fifo 
	
	// we will work with 100ms chunks
	public AudioBuffer(int seconds) {
		
	}
	    = new CircularFifoQueue<Integer>(2);
	    fifo.add(1);
	    fifo.add(2);
	    fifo.add(3);
	    System.out.println(fifo);
//  http://www.java-forums.org/advanced-java/16458-playing-audioinputstream-multiple-times.html
// 
//	byte[] data;
//	InputStream in = ClassLoader.getSystemResourceAsStream(filename); //in an applet
//	AudioInputStream ais = AudioSystem.getAudioInputStream(in);
//	data = new byte[(int)ais.getFrameLength() * format.getFrameSize()];
//	byte[] buf = new byte[BUFSIZE];
//	for (int i=0; i<data.length; i+=BUFSIZE) {
//	    int r = ais.read(buf, 0, BUFSIZE);
//	    if (i+r >= data.length) {
//	        [b]r = data.length - i;[/b]
//	    }
//	    System.arraycopy(buf, 0, data, i, r);
//	}
//	ais.close();
}
