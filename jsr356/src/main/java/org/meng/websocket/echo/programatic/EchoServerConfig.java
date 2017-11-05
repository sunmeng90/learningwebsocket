package org.meng.websocket.echo.programatic;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;
import java.util.HashSet;
import java.util.Set;

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