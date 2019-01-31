package de.factoryfx.data.storage.migration;

public interface GeneralMigration {
    boolean canMigrate(GeneralStorageFormat generalStorageFormat);
    String migrate(String data);
    GeneralStorageFormat migrationResultStorageFormat();
}
