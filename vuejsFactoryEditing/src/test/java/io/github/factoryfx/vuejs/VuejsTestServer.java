package io.github.factoryfx.vuejs;

import de.factoryfx.server.rest.server.JettyServer;

public class VuejsTestServer {

    public final JettyServer jettyServer;


    public VuejsTestServer(JettyServer jettyServer) {
        this.jettyServer = jettyServer;
    }
}