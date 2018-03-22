package uk.me.chriseebee.audiopitchjava;

import java.net.Socket; 
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;

public class SocketClient {

	Socket sock = null;
	OutputStream ostream;
	DataOutputStream dos;
	 
	public SocketClient(String hostname, int port) {
		try {
			sock = new Socket(hostname, port);
		    OutputStream ostream = sock.getOutputStream();                 
		    dos = new DataOutputStream(ostream);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
	}
	
	public void sendMessage(String msg) throws IOException {
		dos.writeBytes(msg);
	}
	
	public void closeAssets() {
		try {
			dos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}              
		
		try {
		     ostream.close();   
		     sock.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}  
		
		try {
		     sock.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}  

	}

	
	
	
}
