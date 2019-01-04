module de.factoryfx.factory {
    //automatic module that should be transitive but can't until they are real modules
    requires jackson.annotations;
    requires com.google.common;

    requires transitive de.factoryfx.data;
    requires transitive org.slf4j;

    exports de.factoryfx.factory;
    exports de.factoryfx.factory.atrribute;
    exports de.factoryfx.factory.exception;
    exports de.factoryfx.factory.log;
    exports de.factoryfx.server;
    exports de.factoryfx.server.user;
    exports de.factoryfx.server.user.nop;
    exports de.factoryfx.server.user.persistent;
    exports de.factoryfx.factory.util;
    exports de.factoryfx.factory.parametrized;
    exports de.factoryfx.factory.builder;


    opens de.factoryfx.factory;//jackson
    opens de.factoryfx.factory.parametrized;
    opens de.factoryfx.factory.atrribute;
    opens de.factoryfx.factory.log;

}