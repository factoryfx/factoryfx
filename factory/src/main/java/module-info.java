module de.factoryfx.factory {
    requires jackson.annotations;
    requires de.factoryfx.data;
    requires slf4j.api;
    requires com.google.common;
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
    opens de.factoryfx.factory.atrribute;
}