package de.factoryfx.factory.datastorage;

public interface FactoryMigration {
    boolean canMigrate(int dataModelVersion);
    String migrate(String data);
    int migrateResultVersion();
}
