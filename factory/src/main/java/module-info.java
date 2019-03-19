module io.github.factoryfx.factory {
    //automatic module that should be transitive but can't until they are real modules
    requires jackson.annotations;
    requires com.google.common;

    requires transitive io.github.factoryfx.data;
    requires transitive org.slf4j;

    exports io.github.factoryfx.factory;
    exports io.github.factoryfx.factory.atrribute;
    exports io.github.factoryfx.factory.exception;
    exports io.github.factoryfx.factory.log;
    exports io.github.factoryfx.server;
    exports io.github.factoryfx.server.user;
    exports io.github.factoryfx.server.user.nop;
    exports io.github.factoryfx.server.user.persistent;
    exports io.github.factoryfx.factory.util;
    exports io.github.factoryfx.factory.parametrized;
    exports io.github.factoryfx.factory.builder;


    opens io.github.factoryfx.factory;//jackson
    opens io.github.factoryfx.factory.parametrized;
    opens io.github.factoryfx.factory.atrribute;
    opens io.github.factoryfx.factory.log;

}