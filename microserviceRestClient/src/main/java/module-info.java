module io.github.factoryfx.microserviceRestClient {
    requires transitive io.github.factoryfx.microserviceRestCommon;
    requires io.github.factoryfx.factory;
    requires jakarta.ws.rs;
    requires jersey.proxy.client;
    requires jersey.client;
    requires jersey.common;
    requires jersey.media.json.jackson;
    requires com.google.common;

    requires com.fasterxml.jackson.databind;
    exports io.github.factoryfx.microservice.rest.client;
}