module io.github.factoryfx.microserviceRestClient {
    requires transitive io.github.factoryfx.microserviceRestCommon;
    requires io.github.factoryfx.factory;
    requires java.ws.rs;
    requires jersey.proxy.client;
    requires jersey.client;
    requires jersey.common;
    requires com.google.common;
    requires com.fasterxml.jackson.jaxrs.json;
    exports io.github.factoryfx.microservice.rest.client;
}