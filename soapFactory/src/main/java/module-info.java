module de.factoryfx.soapFactory {

    requires org.eclipse.jetty.server;
    requires java.logging;

    requires de.factoryfx.factory;
    requires de.factoryfx.jettyFactory;


    requires javax.servlet.api;
    requires java.xml.soap;
    requires java.xml.bind;
    requires java.xml.ws;
    requires javax.jws;

    requires java.annotation;

    exports de.factoryfx.soap;
}