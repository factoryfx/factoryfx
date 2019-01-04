module de.factoryfx.microserviceRestCommon {
    requires transitive de.factoryfx.factory;
    requires jackson.annotations;
    requires java.ws.rs;

    exports de.factoryfx.microservice.common;
}