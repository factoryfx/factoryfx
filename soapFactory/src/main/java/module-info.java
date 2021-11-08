module io.github.factoryfx.soapFactory {

    requires org.eclipse.jetty.server;
    requires java.logging;

    requires io.github.factoryfx.factory;
    requires io.github.factoryfx.jettyFactory;


    requires jakarta.xml.bind;
    requires jakarta.xml.ws;
    requires jakarta.jws;

    requires jakarta.annotation;

    exports io.github.factoryfx.soap;
}