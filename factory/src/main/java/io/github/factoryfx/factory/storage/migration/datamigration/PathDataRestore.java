package io.github.factoryfx.factory.storage.migration.datamigration;


import com.fasterxml.jackson.databind.JsonNode;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/** restore attribute content from one data class to a new one, based on path**/
public class PathDataRestore<R extends FactoryBase<?,?>,V>  {

    private final BiFunction<JsonNode, Map<String, DataJsonNode>,V> valueParser;
    private final AttributePathTarget<V> previousPath;
    private final BiConsumer<R,V> setter;

    public PathDataRestore(AttributePathTarget<V> previousPath, BiConsumer<R, V> setter, BiFunction<JsonNode,Map<String, DataJsonNode>,V> valueParser) {
        this.previousPath = previousPath;
        this.setter=setter;
        this.valueParser = valueParser;
    }

    public boolean canMigrate(DataStorageMetadataDictionary previousDataStorageMetadataDictionary, DataJsonNode root){
        try {
            return previousPath.isPathToRemovedAttribute(previousDataStorageMetadataDictionary, root) || previousPath.isPathToRetypedAttribute(previousDataStorageMetadataDictionary, root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void migrate(DataJsonNode previousRoot, R root) {
        V attributeValue = previousPath.resolveAttributeValue(previousRoot,valueParser);
        setter.accept(root,attributeValue);
    }

}
