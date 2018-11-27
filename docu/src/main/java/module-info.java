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
    requires de.factoryfx.microserviceRestServer;
    requires de.factoryfx.microserviceRestClient;

    opens de.factoryfx.docu.datainjection;
    opens de.factoryfx.docu.dependencyinjection;
    opens de.factoryfx.docu.dynamicwebserver;
    opens de.factoryfx.docu.helloworld;
    opens de.factoryfx.docu.lifecycle;
    opens de.factoryfx.docu.migration;
    opens de.factoryfx.docu.monitoring;
    opens de.factoryfx.docu.parametrized;
    opens de.factoryfx.docu.permission;
    opens de.factoryfx.docu.persistentstorage;
    opens de.factoryfx.docu.polymorphism;
    opens de.factoryfx.docu.reuse;
    opens de.factoryfx.docu.swagger;
}