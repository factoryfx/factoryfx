module io.github.factoryfx.postgresqlStorage {
    requires java.sql;
    requires io.github.factoryfx.data;
    requires com.google.common;
    requires com.fasterxml.jackson.databind;

    exports io.github.factoryfx.factory.datastorage.postgres;
}