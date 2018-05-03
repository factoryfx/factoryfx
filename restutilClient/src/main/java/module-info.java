module de.factoryfx.restutilClient {
    exports de.factoryfx.util.rest.client;
    requires java.ws.rs;
    requires jersey.client;
    requires de.factoryfx.data;
    requires jersey.media.json.jackson;
    requires jersey.common;
    requires de.factoryfx.factory;
    requires com.google.common;
//    requires javax.activation;
}