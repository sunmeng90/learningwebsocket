package org.meng.websocket.echo.codec;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/websocket/codec/echoperson",
        decoders = {PersonDecoder.class},
        encoders = {PersonEncoder.class})
public class EchoCodecEndpoint {

    /**
     *
     * If the return type of the method annotated with @OnMessage is not void, the WebSocket implementation will send
     * the return value to the other peer. The following code snippet returns the received text message in capitals back to the sender:

     @OnMessage
     public String myOnMessage (String txt) {
     return txt.toUpperCase();

     */
    @OnMessage
    public void onMessage(Person person, Session session) {

        try {
            session.getBasicRemote().sendObject(person);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncodeException e) {
            e.printStackTrace();
        }
        System.out.println("Sent ");
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Websocket opened: " + session.getId());
    }

    @OnClose
    public void onClose(CloseReason reason) {
        System.out.println("Closing a websocket due to " + reason.getReasonPhrase());
    }
}
