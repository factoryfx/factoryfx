open module io.github.factoryfx.docu {
    requires io.github.factoryfx.factory;
    requires java.ws.rs;
    requires metrics.core;
    requires io.github.factoryfx.jettyFactory;
    requires metrics.jetty9;
    requires jersey.common;
    requires com.google.common;
    requires ch.qos.logback.classic;

    requires postgresql;
    requires postgresql.embedded;
    requires java.naming;

    requires io.github.factoryfx.postgresqlStorage;
    requires jersey.client;
    requires com.fasterxml.jackson.jaxrs.json;

    requires com.fasterxml.jackson.core;
    requires java.xml.bind;
    requires java.net.http;
    requires io.github.factoryfx.microserviceRestServer;
    requires io.github.factoryfx.microserviceRestClient;
    requires javax.servlet.api;
    requires org.eclipse.jetty.server;
    requires io.github.factoryfx.starter;
    requires io.github.factoryfx.domFactoryEditing;
    requires java.desktop;

}