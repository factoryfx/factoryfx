package de.factoryfx.data.storage.migration;

public interface GeneralMigration {
    boolean canMigrate(GeneralStorageMetadata generalStorageMetadata);
    String migrate(String data);
    GeneralStorageMetadata migrationResultStorageFormat();
}
