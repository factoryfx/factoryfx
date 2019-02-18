module de.factoryfx.postgresqlStorage {
    requires java.sql;
    requires de.factoryfx.data;
    requires com.google.common;
    requires com.fasterxml.jackson.databind;

    exports de.factoryfx.factory.datastorage.postgres;
}