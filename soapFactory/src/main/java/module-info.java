module io.github.factoryfx.soapFactory {

    requires org.eclipse.jetty.server;
    requires java.logging;

    requires io.github.factoryfx.factory;
    requires io.github.factoryfx.jettyFactory;


    requires javax.servlet.api;
    requires java.xml.soap;
    requires java.xml.bind;
    requires java.xml.ws;
    requires java.jws;

    requires java.annotation;

    exports io.github.factoryfx.soap;
}