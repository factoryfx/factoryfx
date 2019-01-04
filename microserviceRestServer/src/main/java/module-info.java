module de.factoryfx.microserviceRestServer {
    requires transitive de.factoryfx.microserviceRestCommon;
    requires transitive de.factoryfx.factory;
    requires java.ws.rs;
    requires jersey.media.jaxb;

    exports de.factoryfx.microservice.rest;
}