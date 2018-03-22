package uk.me.chriseebee.audiopitchjava;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


import org.glassfish.tyrus.server.Server;
 
public class ButtonSocketServer implements Runnable {
	
	String ipAddress = "";
	Server server;
 
    public ButtonSocketServer() {
    	
    	try {
			ipAddress = getFirstNonLoopbackAddress().toString().replace("/","");
			System.out.println("Binding Button Server to: "+ipAddress.toString());
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

    public void stop() {
    	server.stop();
    }
    
	@Override
	public void run() {
		//server = new Server(ipAddress, 9877, "/websockets", null, ButtonSocketServerEndpoint.class);
		server = new Server("10.240.189.74", 9877, "/websockets", null, ButtonSocketServerEndpoint.class);
        try {
            server.start();
            //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            //System.out.println("Please press a key to stop the server.");
            //reader.readLine();
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException(e);
        }
	}
}