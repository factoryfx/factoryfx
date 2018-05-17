module de.factoryfx.microserviceRestServer {
    requires de.factoryfx.factory;
    requires de.factoryfx.data;
    requires java.ws.rs;
    requires de.factoryfx.microserviceRestCommon;
    requires jersey.media.jaxb;

    exports de.factoryfx.microservice.rest;
}