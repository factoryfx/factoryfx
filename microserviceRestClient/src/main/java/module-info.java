module io.github.factoryfx.microserviceRestClient {
    requires transitive io.github.factoryfx.microserviceRestCommon;
    requires io.github.factoryfx.factory;
    requires jakarta.ws.rs;
    requires com.google.common;

    requires com.fasterxml.jackson.databind;
    requires org.glassfish.jersey.core.common;
    requires org.glassfish.jersey.core.client;
    requires org.glassfish.jersey.ext.proxy.client;
    requires org.glassfish.jersey.media.json.jackson;
    exports io.github.factoryfx.microservice.rest.client;
}