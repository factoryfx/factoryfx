module io.github.factoryfx.postgresqlStorage {
    requires io.github.factoryfx.factory;
    requires java.sql;
    requires com.google.common;

    exports io.github.factoryfx.factory.datastorage.postgres;
}