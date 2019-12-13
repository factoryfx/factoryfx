package io.github.factoryfx.factory.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;

/**
 * Patch interface for storage
 */
@FunctionalInterface
public interface DataStoragePatcher {
   /**
    * callback for every stored factory tree
    * @param root factory root, wrapping with DataJsonNode provides a generic factory api {@link io.github.factoryfx.factory.storage.migration.datamigration.DataJsonNode}
    * @param metaData storage metadata {@link io.github.factoryfx.factory.storage.StoredDataMetadata }
    * @param objectMapper mapper used for storing
    */
   void patch(ObjectNode root, JsonNode metaData, SimpleObjectMapper objectMapper);
}
