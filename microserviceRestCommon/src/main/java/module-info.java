module io.github.factoryfx.microserviceRestCommon {
    requires transitive io.github.factoryfx.factory;
    requires jackson.annotations;
    requires java.ws.rs;

    exports io.github.factoryfx.microservice.common;
}