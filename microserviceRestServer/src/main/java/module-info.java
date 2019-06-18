module io.github.factoryfx.microserviceRestServer {
    requires transitive io.github.factoryfx.microserviceRestCommon;
    requires java.ws.rs;
    requires jersey.media.jaxb;

    exports io.github.factoryfx.microservice.rest;
}