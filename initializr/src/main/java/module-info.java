module io.github.factoryfx.starter {
    requires transitive io.github.factoryfx.factory;
    requires com.squareup.javapoet;
    requires java.compiler;
    requires io.github.factoryfx.jettyFactory;
    requires org.eclipse.jetty.server;
    requires java.ws.rs;

    exports io.github.factoryfx.initializr;
    exports io.github.factoryfx.initializr.template;
}