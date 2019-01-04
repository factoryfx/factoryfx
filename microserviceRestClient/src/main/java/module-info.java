module de.factoryfx.microserviceRestClient {
    requires transitive de.factoryfx.microserviceRestCommon;
    requires de.factoryfx.factory;
    requires java.ws.rs;
    requires jersey.proxy.client;
    requires jersey.client;
    requires jersey.common;
    requires com.google.common;
    requires com.fasterxml.jackson.jaxrs.json;
    exports de.factoryfx.microservice.rest.client;
}