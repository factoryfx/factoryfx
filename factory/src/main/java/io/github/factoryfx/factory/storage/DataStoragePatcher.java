package io.github.factoryfx.factory.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;

/**
 * Patch interface for storage
 */
@FunctionalInterface
public interface DataStoragePatcher {
   void patch(ObjectNode root, JsonNode metaData, SimpleObjectMapper objectMapper);
}
