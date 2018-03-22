package uk.me.chriseebee.audiopitchjava;


import java.io.IOException;
import java.time.LocalTime;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.ServerEndpoint;
 
@ServerEndpoint(value = "/data")
public class AudioPitchServerEndpoint {
 
	static ScheduledExecutorService timer = 
		       Executors.newSingleThreadScheduledExecutor(); 
	
    private Logger logger = Logger.getLogger(this.getClass().getName());
 
    private static Set<Session> allSessions; 
    
    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected ... " + session.getId());
        
        allSessions = session.getOpenSessions();
        // start the scheduler on the very first connection
        // to call sendTimeToAll every second   
        if (allSessions.size()==1){   
          timer.scheduleAtFixedRate(
               () -> sendDataToAll(session),0,50,TimeUnit.MILLISECONDS);    
        }
    }
 
    private void sendDataToAll(Session session){       
        allSessions = session.getOpenSessions();
        for (Session sess: allSessions){          
           try{  
        	 String val = CollectionManager.getInstance().queue.poll();
        	 if (val != null)
             sess.getBasicRemote().sendText(val);
             } catch (IOException ioe) {        
                 System.out.println(ioe.getMessage());         
             }   
        }   
     }
    
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }
}