# Websocket
WebSocket is a computer communications protocol, providing full-duplex communication channels over a single TCP connection. WebSocket is a different TCP protocol from HTTP. The WebSocket protocol enables interaction between a browser and a web server with lower overheads, facilitating real-time data transfer from and to the server. 

The WebSocket protocol has two parts: **handshake** and **data transfer**.The client initiates the handshake by sending a request to a WebSocket endpoint using its URI.  

Handshake request from client: 
```js
GET /path/to/websocket/endpoint HTTP/1.1
Host: localhost
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Key: xqBt3ImNzJbYqRINxEFlkg==
Origin: http://localhost
Sec-WebSocket-Version: 13

```

Handshake from server response: 
```js
HTTP/1.1 101 Switching Protocols
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: K7DJLdLooIwIG/MOpvWFB3y3FE8=

```

In a WebSocket application, the server publishes a WebSocket endpoint, and the client uses the endpoint's URI to connect to the server. The WebSocket protocol is symmetrical after the connection has been established; the client and the server can send messages to each other at any time while the connection is open, and they can close the connection at any time. WebSocket endpoints are represented by URIs that have the following form:

```js
ws://host:port/path?query
wss://host:port/path?query
```

The ws scheme represents an unencrypted WebSocket connection, and the wss scheme represents an encrypted connection. The port component is optional; the default port number is 80 for unencrypted connections and 443 for encrypted connections.

The Java EE platform includes the Java API for WebSocket (JSR 356)

The Java API for WebSocket consists of the following packages.
* The javax.websocket.server package contains annotations, classes, and interfaces to create and configure server endpoints.
* The javax.websocket package contains annotations, classes, interfaces, and exceptions that are common to client and server endpoints.


Websocket endpoints are instances of the **javas.websocket.Endpoint** class.  There are two way of creating endpoints: *programmatically* and *annotated based*

The process for creating and deploying a WebSocket endpoint follows.
1. Create an endpoint class.
2. Implement the lifecycle methods of the endpoint.
3. Add your business logic to the endpoint.
4. Deploy the endpoint inside a web application.


```java
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
                    session.getBasicRemote().sendText(serverGreeting+ message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
//Important: register endpoints
public class EchoServerConfig implements ServerApplicationConfig {
    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        Set<ServerEndpointConfig> result = new HashSet<>();
        //create endpoint from EndpointConfig
        ServerEndpointConfig endpointConfig = ServerEndpointConfig.Builder.create(EchoEndpoint.class, "/websocket/programmatic/echo").build();
        result.add(endpointConfig);
        return result;
    }

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        return null;
    }
}

//3..
//4. start the web application.
```


annotation endpoints

```java
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



//Important: register endpoints
public class EchoServerConfig implements ServerApplicationConfig {
    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        Set<ServerEndpointConfig> result = new HashSet<>();
        //create endpoint from EndpointConfig
        ServerEndpointConfig endpointConfig = ServerEndpointConfig.Builder.create(EchoEndpoint.class, "/websocket/programmatic/echo").build();
        result.add(endpointConfig);
        return result;
    }

    //When using annotation endpoint, make sure register the annotation endpoints here, otherwise, client will get 404
    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        //Deploy all annotated endpoints
        Set<Class<?>> result = new HashSet<>();
        for (Class<?> clazz : scanned) {
            if (clazz.getPackage().getName().startsWith("annotation.") || clazz.getPackage().getName().startsWith("org.")) {
                result.add(clazz);
            }
        }
        return result;
    }
}

```

**Note: **  
When using programmatic and annotation endpoints, make sure register the annotation endpoints here, otherwise, client will get 404.

Coverting WebSocket messages from and to custom Java Object Using Encoders and Decoders

An encoder takes a Java object and produces a representation that can be transmitted as a WebSocket message   
* Encoder.Text<T> for text messages
* Encoder.Binary<T> for binary messages 

```java
public class PersonEncoder implements Encoder.Text<Person> {
    @Override
    public String encode(Person object) throws EncodeException {
        JAXBContext jaxbContext = null;
        StringWriter st = null;

        try {
            jaxbContext = JAXBContext.newInstance(Person.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            st = new StringWriter();
            marshaller.marshal(object, st);;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return st.toString();
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
```

A decoder performs the reverse function; it reads a WebSocket message and creates a Java object.   
* Decoder.Text<T> for text messages
* Decoder.Binary<T> for binary messages

```java
public class PersonDecoder implements Decoder.Text<Person> {
    @Override
    public Person decode(String s) throws DecodeException {
        Person person = null;
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Person.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(s);
            person = (Person) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return person;
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
```

Register Encoder and Decoder in ServerEndpoint
```java
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


```

*** Note: *** Like endpoints, decoder instances are associated with one and only one WebSocket connection and peer, so there is only one thread executing the code of a decoder instance at any given time.