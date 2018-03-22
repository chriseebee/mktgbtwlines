package uk.me.chriseebee.audiopitchjava;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.glassfish.tyrus.server.Server;
 
public class AudioPitchServer implements Runnable {
	
	String ipAddress = "";
 
    public AudioPitchServer() {
    	
    	try {
			ipAddress = getFirstNonLoopbackAddress().toString();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
    
   
    private InetAddress getFirstNonLoopbackAddress() throws SocketException {
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface i = (NetworkInterface) en.nextElement();
            for (Enumeration<InetAddress> en2 = i.getInetAddresses(); en2.hasMoreElements();) {
                InetAddress addr = (InetAddress) en2.nextElement();
                if (!addr.isLoopbackAddress()) {
                    if (addr instanceof Inet4Address) {
                        return addr;
                    }
                }
            }
        }
        return null;
    }

	@Override
	public void run() {
		Server server = new Server(ipAddress, 9877, "/websockets", null, AudioPitchServerEndpoint.class);
        
        try {
            server.start();
            //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            //System.out.println("Please press a key to stop the server.");
            //reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            server.stop();
        }
		
	}
}