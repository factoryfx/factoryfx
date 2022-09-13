module io.github.factoryfx.jettyFactory {
    //automatic module that should be transitive but can't until they are real modules
    requires jakarta.ws.rs;
    requires jetty.servlet.api;
    requires com.google.common;
    requires jersey.common;
    requires com.fasterxml.jackson.jakarta.rs.json;
    requires jersey.server;
    requires jersey.container.servlet.core;
    requires jersey.media.json.jackson;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.util;
    requires org.eclipse.jetty.servlet;
    requires org.eclipse.jetty.http;
    requires org.eclipse.jetty.http2.server;
    requires org.eclipse.jetty.alpn.server;

    requires java.logging;
    requires transitive io.github.factoryfx.factory;
    requires com.fasterxml.jackson.databind;

    exports io.github.factoryfx.jetty;
    exports io.github.factoryfx.jetty.ssl;
    exports io.github.factoryfx.jetty.builder;
}