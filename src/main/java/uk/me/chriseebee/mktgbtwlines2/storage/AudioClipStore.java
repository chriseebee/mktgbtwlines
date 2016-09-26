package uk.me.chriseebee.mktgbtwlines2.storage;

import java.io.ByteArrayOutputStream;

import java.io.IOException;

import java.util.Collection;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.audio.TimedAudioBuffer;

/**
 * This class is used to store the buffers we receive
 * from the audioinputstream
 * 
 * It abstracts from the calling classes where the audio is 
 * although that should be
 * 
 * 1. In a memory store that can be searched fast, then, when older
 *
 * 2. On the file system, where the objects are synchronized to a file
 *
 * @author cbell
 *
 */
public class AudioClipStore {
	
	Logger logger = LoggerFactory.getLogger(AudioClipStore .class);
	
	private static AudioClipStore _instance = null;
	//private static final String dataDirName = "data_files";
	private static NavigableMap<Long, TimedAudioBuffer> fileMap = new TreeMap<Long,TimedAudioBuffer>();
	
	private AudioClipStore() {}
	
	public static AudioClipStore getInstance () {
		if (_instance == null) {
			_instance = new AudioClipStore();
		}
		
		return _instance;
	}


	public void put (TimedAudioBuffer tab) {
		fileMap.put(tab.getStartDateTime(), tab);
	}
	
	// make sure that the caller adds an extra chunk to the endTime
	// otherwise it might truncate
	public void get (TimedAudioBuffer tabIn) {
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		Collection<TimedAudioBuffer> tabs = fileMap.subMap(tabIn.getStartDateTime(),tabIn.getEndDateTime()).values();
		for (TimedAudioBuffer tab : tabs) {
			try {
				outputStream.write(tab.getBuffer());
			} catch (IOException e) {
				logger.error("Failed to write buffer to outputstream",e);
			}
		}
		tabIn.setBuffer(outputStream.toByteArray());
		logger.info("Constructed byte array for "+tabIn.getLength()/1000+" seconds, which is "+tabIn.getBuffer().length+ " bytes");
	}
//	
//	public TimedAudioBuffer getAudioBufferNearestTime(long time) {
//		TimedAudioBuffer tab  = null;
//		try {
//			try {
//				tab = (TimedAudioBuffer) deserialize(time+".tab");
//				return tab;
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return tab;
//		
//	}
	
//	public void storeAudioBuffer(TimedAudioBuffer tab) {
//
//		try {
//			serialize(tab, tab.getStartDateTime()+".tab");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	// deserialize to Object from given file
//	private Object deserialize(String fileName) throws IOException,
//			ClassNotFoundException {
//		FileInputStream fis = new FileInputStream(fileName);
//		ObjectInputStream ois = new ObjectInputStream(fis);
//		Object obj = ois.readObject();
//		ois.close();
//		return obj;
//	}
//
//	// serialize the given object and save it to file
//	private void serialize(Object obj, String fileName)
//			throws IOException {
//		FileOutputStream fos = new FileOutputStream(fileName);
//		ObjectOutputStream oos = new ObjectOutputStream(fos);
//		oos.writeObject(obj);
//
//		fos.close();
//	}

	
}
