package org.meng.websocket.echo;

import org.meng.websocket.echo.programatic.EchoEndpoint;

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

    //When using annotation endpoint, make sure register the annotation endpoints here, otherwise, client will get 404
    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        //Deploy all annotated endpoints
        Set<Class<?>> result = new HashSet<>();
        for (Class<?> clazz : scanned) {
            System.out.println("package: "+clazz.getPackage());
            if (clazz.getPackage().getName().startsWith("annotation.") || clazz.getPackage().getName().startsWith("org.")) {
                result.add(clazz);
            }
        }
        return result;
    }
}
