module io.github.factoryfx.jettyFactory {
    //automatic module that should be transitive but can't until they are real modules
    requires java.ws.rs;
    requires javax.servlet.api;
    requires com.google.common;
    requires com.fasterxml.jackson.databind;
    requires jersey.common;
    requires com.fasterxml.jackson.jaxrs.json;
    requires jersey.server;
    requires jersey.container.servlet.core;
    requires jackson.annotations;
    requires jersey.media.jaxb;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.util;
    requires org.eclipse.jetty.servlet;
    requires org.eclipse.jetty.http;

    requires java.logging;
    requires transitive io.github.factoryfx.factory;

    exports io.github.factoryfx.jetty;
    exports io.github.factoryfx.jetty.ssl;
    exports io.github.factoryfx.jetty.builder;
}