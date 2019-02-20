package de.factoryfx.data.storage.migration;

import com.fasterxml.jackson.databind.JsonNode;

public interface GeneralMigration {
    boolean canMigrate(GeneralStorageMetadata generalStorageMetadata);
    void migrate(JsonNode data);
    GeneralStorageMetadata migrationResultStorageFormat();
}
