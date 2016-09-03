package uk.me.chriseebee.mktgbtwlines2.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.NavigableSet;
import java.util.TreeSet;

import uk.me.chriseebee.mktgbtwlines2.audio.TimedAudioBuffer;

/**
 * THis class is used to store the buffers we receive
 * from the audioinputstream
 * 
 * It abstracts from the calling classes where the audio is 
 * although that should be
 * 
 * 1. In a queue immediately for consumption by the speech to text service
 *    being used
 * 2. On the file system, where the objects are synchronized to a file
 * @author cbell
 *
 */
public class AudioClipStore {
	
	private static final String dataDirName = "data_files";
	private static NavigableSet<Long> fileLongDates = new TreeSet<Long>();

	
	public AudioClipStore() {
		
	}
	
	public TimedAudioBuffer getAudioBufferNearestTime(long time) {
		TimedAudioBuffer tab  = null;
		try {
			try {
				tab = (TimedAudioBuffer) deserialize(time+".tab");
				return tab;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tab;
		
	}
	
	public void storeAudioBuffer(TimedAudioBuffer tab) {

		try {
			serialize(tab, tab.getStartDateTime()+".tab");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// deserialize to Object from given file
	private Object deserialize(String fileName) throws IOException,
			ClassNotFoundException {
		FileInputStream fis = new FileInputStream(fileName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object obj = ois.readObject();
		ois.close();
		return obj;
	}

	// serialize the given object and save it to file
	private void serialize(Object obj, String fileName)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(obj);

		fos.close();
	}

	
}
