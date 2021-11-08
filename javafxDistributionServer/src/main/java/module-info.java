module io.github.factoryfx.javafxDistributionServer {
    requires transitive io.github.factoryfx.factory;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.util;
    requires jakarta.ws.rs;
    requires com.google.common;

    exports io.github.factoryfx.javafx.distribution.launcher.rest;
    exports io.github.factoryfx.javafx.distribution.server.rest;
}