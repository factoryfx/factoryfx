module de.factoryfx.docu {
    requires de.factoryfx.factory;
    requires de.factoryfx.data;
    requires java.ws.rs;
    requires metrics.core;
    requires de.factoryfx.jettyFactory;
    requires metrics.jetty9;
    requires jersey.common;
    requires com.google.common;
    requires logback.classic;
    requires de.factoryfx.restutilClient;
    requires slf4j.api;

    requires postgresql;
    requires postgresql.embedded;
    requires java.naming;

    requires de.factoryfx.postgresqlStorage;

}