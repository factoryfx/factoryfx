package de.factoryfx.data.storage;

public interface DataMigration {
    boolean canMigrate(int dataModelVersion);
    String migrate(String data);
    int migrateResultVersion();
}
