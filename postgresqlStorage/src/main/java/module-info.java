module io.github.factoryfx.postgresqlStorage {
    requires io.github.factoryfx.factory;
    requires java.sql;
    requires com.google.common;
    requires com.fasterxml.jackson.databind;

    exports io.github.factoryfx.factory.datastorage.postgres;
}