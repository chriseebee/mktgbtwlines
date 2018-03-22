package uk.me.chriseebee.mktgbtwlines2.storage;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.UUID;
import java.sql.*;
import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.audio.TimedAudioBuffer;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigurationException;
import uk.me.chriseebee.mktgbtwlines2.nlp.NamedEntityManager;

/**
 * This class is used to store the buffers we receive from the audioinputstream
 * 
 * It abstracts from the calling classes where the audio is although that should
 * be
 * 
 * 1. In a memory store that can be searched fast, then, when older
 *
 * 2. On the file system, where the objects are synchronized to a file
 *
 * @author cbell
 *
 */
public class AudioClipStore {

	Logger logger = LoggerFactory.getLogger(AudioClipStore.class);

	private static AudioClipStore _instance = null;
	// private static final String dataDirName = "data_files";
	private static NavigableMap<Long, TimedAudioBuffer> fileMap = new TreeMap<Long, TimedAudioBuffer>();

	private Path storageDir = null;
	private Connection conn = null;
	private static PreparedStatement pstmtInsertClip = null;
	private static PreparedStatement pstmtInsertBlock = null;
	private static PreparedStatement pstmtRetrieve = null;
	private static PreparedStatement pstmtClipCount = null;

	private String storeType;
	public static String STORE_TYPE_MEMORY = "MEMORY";
	public static String STORE_TYPE_DATABASE = "DATABASE";

	private AudioClipStore() throws Exception {
		logger.info("Initializing AudioClipStore");
		try {
			logger.info(" - Connecting to DB");
			this.connectDatabase();
			logger.info(" - Checking Setup of DB");
			setupDB();
			logger.info(" - Preparing Statement 1");
			pstmtInsertClip = conn.prepareStatement("insert into audio_clips (start_time, end_time, audio_bytes_file) values (?,?,?)");
			logger.info(" - Preparing Statement 2");
			pstmtRetrieve = conn.prepareStatement("select ID, audio_bytes_file from audio_clips where start_time between ? and ?");
			logger.info(" - Preparing Statement 3");
			pstmtClipCount = conn.prepareStatement("select count(*) from audio_clips");
			logger.info(" - Preparing Statement 4");
			pstmtInsertBlock = conn.prepareStatement("insert into AUDIO_BLOCKS (start_time,end_time) values (?,?)");
			//pstmtAssociate = conn.prepareStatement("insert into AUDIO_CLIP_BLOCK_MAP (block_id, clip_id) values (?,?)");
			
		} catch (Exception e) {
			logger.error("Cannot connect to database or prepare statements",e);
			throw new Exception ("Cannot connect to database or prepare statements");
		}
		
		logger.info(" - Preparing Data Directory");
		try {
			storageDir = Paths.get(ConfigLoader.getConfig().getAudioClipStorageOptions().get("audioClipDir"));
			if (storageDir.toFile().exists()) {
				if (!storageDir.toFile().isDirectory()) {
					throw new ConfigurationException ("File location for saving audio clips is not a directory");
				}
			} else {
				java.nio.file.Files.createDirectory(storageDir);
			}
		} catch (FileNotFoundException fnfe) {
			logger.error("Cannot stat the file location for saving audio clips");
			throw new ConfigurationException ("Cannot stat the file location for saving audio clips");
		} catch (Exception e) {
			logger.error("Other error getting the file location for saving audio clips",e);
		}
		
		logger.info("Initialized AudioClipStore");

	}
	
	public static AudioClipStore getInstance(String storeType) throws Exception {
		if (_instance == null) {
			_instance = new AudioClipStore();
			_instance.storeType = storeType;
		}

		return _instance;
	}

	public void put(TimedAudioBuffer tab) {
		logger.trace("Storing Audio Clip");
		if (storeType.equals(AudioClipStore.STORE_TYPE_MEMORY)) {
			fileMap.put(tab.getStartDateTime(), tab);
		} else {
			try {
				insertClip(tab);
				//printClipClount();
			} catch (Exception e) {
				logger.error("Failed to write buffer to database", e);
			}
		}
		
	}

	// make sure that the caller adds an extra chunk to the endTime
	// otherwise it might truncate

	public void get(TimedAudioBuffer tabIn) throws Exception {
		if (storeType.equals(AudioClipStore.STORE_TYPE_MEMORY)) {
			getMemory(tabIn);
		} else {
			getDatabase(tabIn);
		}
	}

	private void getMemory(TimedAudioBuffer tabIn) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Collection<TimedAudioBuffer> tabs = fileMap.subMap(tabIn.getStartDateTime(), tabIn.getEndDateTime()).values();
		for (TimedAudioBuffer tab : tabs) {
			try {
				outputStream.write(tab.getBuffer());
			} catch (IOException e) {
				logger.error("Failed to write buffer to outputstream", e);
			}
		}
		tabIn.setBuffer(outputStream.toByteArray());
		logger.info("Constructed byte array for " + tabIn.getLength() / 1000 + " seconds, which is "
				+ tabIn.getBuffer().length + " bytes");
	}

	private void getDatabase(TimedAudioBuffer tabIn) throws Exception {
		
		insertBlock(tabIn);
		
		//if (blockId==0) { throw new Exception ("Could not insert block"); }
		
		ResultSet rs = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			pstmtRetrieve.setTimestamp(1, new Timestamp(tabIn.getStartDateTime()));
			pstmtRetrieve.setTimestamp(2, new Timestamp(tabIn.getEndDateTime()));
			rs = pstmtRetrieve.executeQuery();

			while (rs.next()) {
				long clipId = rs.getLong(1);
				outputStream.write(rs.getBytes(2));
				//mapClipToBlock(blockId, clipId);
			}
		} catch (SQLException sqle) {

		} finally {
			this.closeResultSet(rs);
		}

		tabIn.setBuffer(outputStream.toByteArray());
		logger.info("Constructed byte array for " + tabIn.getLength() / 1000 + " seconds, which is "
				+ tabIn.getBuffer().length + " bytes");
	}
	
	private void setupDB() {
		createTableIfNotExists("audio_clips","/db/V1__CreateAudioClipTable.sql");
		createTableIfNotExists("audio_blocks","/db/V2__CreateAudioBlockTable.sql");
	}
	
	public void createTableIfNotExists(String tableName,String ddl) {
		if (!doesTableExist(tableName)) {
			logger.info(" - No it doesn't");
			try {
				Reader r = new BufferedReader(new InputStreamReader(NamedEntityManager.class.getResourceAsStream(ddl)));
				RunScript.execute(conn,r);
				logger.info(" - Created the table!");
			} catch (SQLException sqle) {
				logger.error("Error getting table status from DB",sqle);
			}
		}
			
	}
	
	public boolean doesTableExist(String tableName) {
		logger.info("Checking table exists: "+tableName);
		boolean exists = false;
		ResultSet rs = null;
		try {
			PreparedStatement pstmt = conn.prepareStatement("select 1 from information_schema.tables where table_name = ?");
			pstmt.setString(1, tableName.toUpperCase());
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				// if there is a record in resultset then it exists, simple
				exists = true;
				logger.info(" - Yes it does");
			}
			
		} catch (SQLException sqle) {
			logger.error("Error getting table status from DB",sqle);
		} finally {
			this.closeResultSet(rs);
		}
		
		return exists;
	}

//	public void mapClipToBlock(long blockId, long clipId) {
//		try {
//			pstmtAssociate.setLong(1, blockId);
//			pstmtAssociate.setLong(2, clipId);
//			pstmtAssociate.executeUpdate();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	public void insertClip(TimedAudioBuffer tab) throws Exception {
		try {
		
			logger.debug("About to Insert into Database");
			pstmtInsertClip.setTimestamp(1, new Timestamp(tab.getStartDateTime()));
			pstmtInsertClip.setTimestamp(2, new Timestamp(tab.getEndDateTime()));
			String filename = storageDir+"/"+(UUID.randomUUID().toString())+".buf";
			Path path = Paths.get(filename);
			java.nio.file.Files.createFile(path);
		    java.nio.file.Files.write(path, tab.getBuffer());
		    pstmtInsertClip.setString(3, filename);
			int affectedRows = pstmtInsertClip.executeUpdate();

			conn.commit();
			
			if (affectedRows == 0) {
				throw new Exception("Creating clip failed, no rows affected.");
			}
			
			logger.debug("Inserted into Database: "+affectedRows);
			
		} catch (SQLException e) {
			logger.error("Failed to insert Audio Clip",e);
			throw e;
		} 

	}
	
	public void printClipClount() {
		ResultSet rs = null;
		try {
			
			rs = pstmtClipCount.executeQuery();

			while (rs.next()) {
				logger.info("Clip Count = "+rs.getInt(1));
			}

		} catch (SQLException sqle) {
			
		} finally {
			this.closeResultSet(rs);
		}
	}

	public void insertBlock(TimedAudioBuffer tab) throws Exception 	{
		
		long blockId;
		
		pstmtInsertBlock.setTimestamp(1, new Timestamp(tab.getStartDateTime()));
		pstmtInsertBlock.setTimestamp(2, new Timestamp(tab.getEndDateTime()));

		int affectedRows = pstmtInsertClip.executeUpdate();
		
		conn.commit();

		if (affectedRows == 0) {
			throw new Exception("Creating block failed, no rows affected.");
		}
		
//		try (ResultSet generatedKeys = pstmtInsertBlock.getGeneratedKeys()) {
//            if (generatedKeys.next()) {
//            	blockId  = generatedKeys.getLong(1);
//            }
//            else {
//                throw new Exception("Creating block failed, no ID obtained.");
//            }
//        }
//		
//		return blockId;

	}

	public void connectDatabase() throws Exception {
		Class.forName("org.h2.Driver");
		conn = DriverManager.getConnection("jdbc:h2:~/myh2", "", "");
	}

	public void closeDatabase() throws Exception {
		logger.info("Shutting down database");
		closeAllStatements();
		conn.close();
	}
	
	private void closeResultSet(ResultSet rs) {
		try {
			if (rs!=null) {
				if (!rs.isClosed()) {
					rs.close();
				}
			}
		} catch (SQLException e) {
			logger.error("Could not close ResultSet",e);
		}
	}
	
	private void closeAllStatements() {
		try {
			pstmtInsertClip.close();
		} catch (SQLException e) {
			// do nothing
		}
		try {
			pstmtInsertBlock.close();
		} catch (SQLException e) {
			// do nothing
		}
		try {
			pstmtRetrieve.close();
		} catch (SQLException e) {
			// do nothing
		}
		try {
			pstmtClipCount.close();
		} catch (SQLException e) {
			// do nothing
		}
	}
	

//	JdbcConnectionPool cp = JdbcConnectionPool.create("jdbc:h2:~/test", "sa", "sa");
//	
//	for(int i = 0;i<args.length;i++)
//	{
//		Connection conn = cp.getConnection();
//		conn.createStatement().execute(args[i]);
//		conn.close();
//	}cp.dispose();
//}

//
// public TimedAudioBuffer getAudioBufferNearestTime(long time) {
// TimedAudioBuffer tab = null;
// try {
// try {
// tab = (TimedAudioBuffer) deserialize(time+".tab");
// return tab;
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// } catch (ClassNotFoundException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
//
// return tab;
//
// }

// public void storeAudioBuffer(TimedAudioBuffer tab) {
//
// try {
// serialize(tab, tab.getStartDateTime()+".tab");
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// }

// // deserialize to Object from given file
// private Object deserialize(String fileName) throws IOException,
// ClassNotFoundException {
// FileInputStream fis = new FileInputStream(fileName);
// ObjectInputStream ois = new ObjectInputStream(fis);
// Object obj = ois.readObject();
// ois.close();
// return obj;
// }
//
// // serialize the given object and save it to file
// private void serialize(Object obj, String fileName)
// throws IOException {
// FileOutputStream fos = new FileOutputStream(fileName);
// ObjectOutputStream oos = new ObjectOutputStream(fos);
// oos.writeObject(obj);
//
// fos.close();
// }

}
