module de.factoryfx.jettyFactory {

    requires java.ws.rs;
    requires org.slf4j;
    requires javax.servlet.api;
    requires de.factoryfx.factory;
    requires org.eclipse.jetty.util;
    requires de.factoryfx.data;
    requires com.google.common;
    requires com.fasterxml.jackson.databind;
    requires java.logging;
    requires org.eclipse.jetty.server;
    requires jersey.common;
    requires com.fasterxml.jackson.jaxrs.json;
    requires org.eclipse.jetty.servlet;
    requires jersey.server;
    requires jersey.container.servlet.core;
    requires jackson.annotations;
    requires jersey.media.jaxb;
    requires org.eclipse.jetty.http;

    exports de.factoryfx.jetty;
    exports de.factoryfx.jetty.ssl;
}