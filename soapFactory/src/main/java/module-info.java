module de.factoryfx.soapFactory {
    requires java.annotation;

//    requires java.xml.bind;
//    requires jetty.server;
//    requires javax.servlet.api;
//    requires de.factoryfx.factory;

//    requires java.se.ee;

    requires jetty.server;

//    requires java.xml.soap;
//    requires jsr181.api;

    requires java.logging;
    requires javax.servlet.api;
    requires de.factoryfx.factory;
    requires java.xml.bind;
    requires javax.jws;
    requires java.xml.soap;

    exports de.factoryfx.soap;
}