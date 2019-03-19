module io.github.factoryfx.microserviceRestServer {
    requires transitive io.github.factoryfx.microserviceRestCommon;
    requires transitive io.github.factoryfx.factory;
    requires java.ws.rs;
    requires jersey.media.jaxb;

    exports io.github.factoryfx.microservice.rest;
}