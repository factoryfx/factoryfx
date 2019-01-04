module de.factoryfx.jettyFactory {
    //automatic module that should be transitive but can't until they are real modules
    requires java.ws.rs;
    requires javax.servlet.api;
    requires org.eclipse.jetty.util;
    requires com.google.common;
    requires com.fasterxml.jackson.databind;
    requires org.eclipse.jetty.server;
    requires jersey.common;
    requires com.fasterxml.jackson.jaxrs.json;
    requires org.eclipse.jetty.servlet;
    requires jersey.server;
    requires jersey.container.servlet.core;
    requires jackson.annotations;
    requires jersey.media.jaxb;
    requires org.eclipse.jetty.http;

    requires java.logging;
    requires transitive de.factoryfx.factory;

    exports de.factoryfx.jetty;
    exports de.factoryfx.jetty.ssl;
}