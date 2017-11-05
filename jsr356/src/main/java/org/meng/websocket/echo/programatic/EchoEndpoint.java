package org.meng.websocket.echo.programatic;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.IOException;

//1. create endpoint class
public class EchoEndpoint extends Endpoint {
    private final String serverGreeting = "Server => ";

    //2. Implement the lifecycle methods of endpoint
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        //2.1 get the session between this endpoint and remote endpoints
        //2.2 register message handler for this session
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            //onMessage method of the message handler is invoked when the endpoint receives a text message.
            @Override
            public void onMessage(String message) {
                try {
                    //2.3 the getBasicRemote method returns an object that represents the remote endpoint
                    session.getBasicRemote().sendText(serverGreeting + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}