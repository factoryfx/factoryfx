package io.github.factoryfx.factory.storage;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Patch interface for storage
 */
@FunctionalInterface
public interface DataStoragePatcher {
   void patch(JsonNode data, JsonNode metaData);
}
