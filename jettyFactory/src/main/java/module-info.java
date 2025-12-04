module io.github.factoryfx.jettyFactory {
    //automatic module that should be transitive but can't until they are real modules
    requires jakarta.ws.rs;
    requires com.google.common;
    requires com.fasterxml.jackson.jakarta.rs.json;

    requires org.glassfish.jersey.media.json.jackson;
    requires org.glassfish.jersey.core.client;
    requires org.glassfish.jersey.core.common;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.util;
    requires org.eclipse.jetty.ee10.servlet;
    requires org.eclipse.jetty.http;
    requires org.eclipse.jetty.http2.server;
    requires org.eclipse.jetty.alpn.server;

    requires java.logging;
    requires transitive io.github.factoryfx.factory;
    requires com.fasterxml.jackson.databind;
    requires ini4j;
    requires org.glassfish.jersey.core.server;
    requires org.glassfish.jersey.container.servlet;
    requires org.eclipse.jetty.compression.server;
    requires org.eclipse.jetty.compression.gzip;

    exports io.github.factoryfx.jetty;
    exports io.github.factoryfx.jetty.ssl;
    exports io.github.factoryfx.jetty.builder;
}