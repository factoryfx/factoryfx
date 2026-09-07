package io.github.factoryfx.factory.storage.migration;

import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.factory.storage.migration.datamigration.DataJsonNode;

/**
 * a patch applied to the stored json of a configuration.<br>
 * registered on the {@link io.github.factoryfx.factory.builder.MicroserviceBuilder} (withPatch): applied in-memory
 * whenever a configuration (current or history) is loaded, after the declarative migrations and the removal handling,
 * so the patch sees the stored configuration lifted to the current model shape. The stored data is not changed unless
 * the patches are explicitly persisted via {@link io.github.factoryfx.server.MicroserviceDeployment#persistConfigurationPatches()}
 * (which uses {@link io.github.factoryfx.factory.storage.DataStorage#patchAll} to write the results back to the storage).
 */
@FunctionalInterface
public interface ConfigurationPatch {
    /**
     * callback for a factory tree
     * @param root factory root json wrapped as {@link DataJsonNode} which provides a generic factory api
     * @param metadata storage metadata of the configuration, changes to {@link StoredDataMetadata#configurationSchemaVersion} are persisted by the storage patch api
     * @param objectMapper mapper used for storing
     */
    void patch(DataJsonNode root, StoredDataMetadata metadata, SimpleObjectMapper objectMapper);
}
