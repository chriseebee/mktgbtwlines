package uk.me.chriseebee.mktgbtwlines.speech2text.google;

import io.grpc.ManagedChannel;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.chriseebee.mktgbtwlines2.audio.AudioUtils;

import com.examples.cloud.speech.AsyncRecognizeClient;
import com.examples.cloud.speech.StreamingRecognizeClient;

public class GoogleClientApp {

	Logger logger = LoggerFactory.getLogger(GoogleClientApp.class);
	
	AudioUtils au;
	String audioFile = "";
    String host = "speech.googleapis.com";
    Integer port = 443;
    Integer sampling = 16000;
	
	public GoogleClientApp() {

	    
	}
	
	public void doWork() {
		au = new AudioUtils();
		
	    ManagedChannel channel;
		try {
			
			channel = AsyncRecognizeClient.createChannel(host, port);
		    StreamingRecognizeClient client = null;
		    
		    au.startRecording();
		    
			try {
				client = new StreamingRecognizeClient(channel, au.getAudioInputStream(), sampling);
				Date startDate = new Date();
				System.out.println("Time = "+startDate.toString());
				client.setup();
				client.recognizeParent();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Google Streaming Input Stream Recognize Error",e);
			} finally {
		      try {
					client.shutdown();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Google Streaming Shutdown Error",e);
				}
		    }
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Google Streaming IO Error",e);
		}

	}
	
    public static void main( String[] args )
    {
    	GoogleClientApp gca = new GoogleClientApp();
    	
    	gca.doWork();
    	
//	    CommandLineParser parser = new DefaultParser();
//
//	    Options options = new Options();
//	    options.addOption(
//	        OptionBuilder.withLongOpt("file")
//	            .withDescription("path to audio file")
//	            .hasArg()
//	            .withArgName("FILE_PATH")
//	            .create());
//	    options.addOption(
//	        OptionBuilder.withLongOpt("host")
//	            .withDescription("endpoint for api, e.g. speech.googleapis.com")
//	            .hasArg()
//	            .withArgName("ENDPOINT")
//	            .create());
//	    options.addOption(
//	        OptionBuilder.withLongOpt("port")
//	            .withDescription("SSL port, usually 443")
//	            .hasArg()
//	            .withArgName("PORT")
//	            .create());
//	    options.addOption(
//	        OptionBuilder.withLongOpt("sampling")
//	            .withDescription("Sampling Rate, i.e. 16000")
//	            .hasArg()
//	            .withArgName("RATE")
//	            .create());
//
//	    try {
//	      CommandLine line = parser.parse(options, args);
//	      if (line.hasOption("file")) {
//	        gca.setAudioFile(line.getOptionValue("file"));
//	      } else {
//	        System.err.println("An Audio file must be specified (e.g. /foo/baz.raw).");
//	        System.exit(1);
//	      }
//
//	      if (line.hasOption("host")) {
//	        gca.setHost(line.getOptionValue("host"));
//	      } else {
//	        System.err.println("An API enpoint must be specified (typically speech.googleapis.com).");
//	        System.exit(1);
//	      }
//
//	      if (line.hasOption("port")) {
//	        gca.setPort(Integer.parseInt(line.getOptionValue("port")));
//	      } else {
//	        System.err.println("An SSL port must be specified (typically 443).");
//	        System.exit(1);
//	      }
//
//	      if (line.hasOption("sampling")) {
//	        gca.setSampling(Integer.parseInt(line.getOptionValue("sampling")));
//	      } else {
//	        System.err.println("An Audio sampling rate must be specified.");
//	        System.exit(1);
//	      }
//	    } catch (ParseException exp) {
//	      System.err.println("Unexpected exception:" + exp.getMessage());
//	      System.exit(1);
//	    }

    }

	public String getAudioFile() {
		return audioFile;
	}

	public void setAudioFile(String audioFile) {
		this.audioFile = audioFile;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getSampling() {
		return sampling;
	}

	public void setSampling(Integer sampling) {
		this.sampling = sampling;
	}
}
