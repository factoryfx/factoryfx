module de.factoryfx.postgresqlStorage {
    requires java.sql;
    requires de.factoryfx.data;
    requires com.google.common;

    exports de.factoryfx.factory.datastorage.postgres;
}