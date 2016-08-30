package uk.me.chriseebee.mktgbtwlines.speech2text.ibm;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;

import uk.me.chriseebee.mktgbtwlines.speech2text.TestFilesSetup;
import uk.me.chriseebee.mktgbtwlines2.config.ConfigLoader;
import uk.me.chriseebee.mktgbtwlines2.config.mappers.AppConfig;

public class WatsonClientTest {
	
	Logger logger = LoggerFactory.getLogger(WatsonClientTest.class);

	@Test
	public void test() {
		
		TestFilesSetup tfs = new TestFilesSetup();
		AppConfig ac = null; 
		try {
			ConfigLoader cl = ConfigLoader.getConfigLoader();
			ac = cl.getConfig();
		} catch (Exception e) {
			e.printStackTrace();
			fail("BIG ERROR LOADING CONFIG");
		}
		
		WatsonClient wc = new WatsonClient(
				 ac.getWatsonKeys().get("username")
				,ac.getWatsonKeys().get("password")
				);
		
		for (int i=0;i<6;i++) {
			logger.info("File "+i);
			URI uri = null;
			try {
				uri = this.getClass().getResource("/"+tfs.getFileNameList().get(i)).toURI();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SpeechResults res = wc.processFile(new File(uri));
			for (Transcript t: res.getResults()) {
				if (t.isFinal()) {
					logger.info(t.getAlternatives().get(0).getTranscript());
				}
			}
		}
		
		fail("Not yet implemented");
	}

}
