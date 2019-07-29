package io.github.factoryfx.example.server;


import io.github.factoryfx.jetty.JettyServerFactory;

public class ServerRootFactory extends JettyServerFactory<ServerRootFactory> {

    public ServerRootFactory(){
        this.config().setDisplayTextProvider(()->"Server");
    }
}
