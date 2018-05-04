module de.factoryfx.restutilClient {
    requires java.ws.rs;
    requires jersey.client;
    requires de.factoryfx.data;
    requires jersey.media.json.jackson;
    requires jersey.common;
    requires de.factoryfx.factory;
    requires com.google.common;

    exports de.factoryfx.util.rest.client;
}