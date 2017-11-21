package org.meng.websocket.echo.codec;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/websocket/codec/echoperson",
        decoders = {PersonDecoder.class},
        encoders = {PersonEncoder.class})
public class EchoCodecEndpoint {

    @OnMessage
    public Person onMessage(Person person, Session session) {

        try {
            session.getBasicRemote().sendObject(person);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncodeException e) {
            e.printStackTrace();
        }
        System.out.println("Sent ");
        return person;
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
