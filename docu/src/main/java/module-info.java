module de.factoryfx.docu {
    requires de.factoryfx.factory;
    requires de.factoryfx.data;
    requires java.ws.rs;
    requires metrics.core;
    requires de.factoryfx.jettyFactory;
    requires metrics.jetty9;
    requires jersey.common;
    requires com.google.common;
    requires ch.qos.logback.classic;
    requires org.slf4j;

    requires postgresql;
    requires postgresql.embedded;
    requires java.naming;

    requires de.factoryfx.postgresqlStorage;
    requires jersey.client;
    requires com.fasterxml.jackson.jaxrs.json;

    requires swagger.annotations;
    requires swagger.models;
    requires swagger.jaxrs;
    requires swagger.core;
    requires com.fasterxml.jackson.core;
    requires java.xml.bind;
    requires java.net.http;

    exports de.factoryfx.docu.dynamicwebserver;
    exports de.factoryfx.docu.reuse;
    exports de.factoryfx.docu.swagger;
    opens de.factoryfx.docu.helloworld;
}