package org.meng.websocket.echo.annotation;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/websocket/annotation/echo")
public class AnnotatedEchoEndpoint {
    private final String serverGreeting = "Server => ";

    @OnOpen
    public void onOpen(Session session){
        System.out.println("start annotation endpoint...");
    }

    @OnMessage
    public void onMessage(String msg, Session session) {
        try {
            if(session.isOpen()){
                session.getBasicRemote().sendText(serverGreeting + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
