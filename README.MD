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