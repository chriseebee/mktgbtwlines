package uk.me.chriseebee.audiopitchjava;

import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
 
@ServerEndpoint(value = "/button")
public class ButtonSocketServerEndpoint {
	
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected ... " + session.getId());
        
    }
    
    @OnMessage
    public void processMessage(String message, Session session) {
        System.out.println("Greeting received:" + message);
//        if (message.equals("1")) {
//        	CollectionManager.getInstance().setLaughing(true);
//        } else {
//        	CollectionManager.getInstance().setLaughing(false);
//        }
        
    }
    
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }
}