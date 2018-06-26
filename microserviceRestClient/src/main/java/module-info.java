module de.factoryfx.microserviceRestClient {
    requires de.factoryfx.data;
    requires de.factoryfx.factory;
    requires de.factoryfx.microserviceRestCommon;
    requires java.ws.rs;
    requires com.fasterxml.jackson.jaxrs.json;
    requires jersey.proxy.client;
    requires jersey.client;
    requires jersey.common;
    requires com.google.common;
    exports de.factoryfx.microservice.rest.client;
}