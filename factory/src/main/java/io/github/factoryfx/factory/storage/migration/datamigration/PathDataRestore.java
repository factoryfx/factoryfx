package io.github.factoryfx.factory.storage.migration.datamigration;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.function.BiConsumer;

/** restore attribute content from one data class to a new one, based on path**/
public class PathDataRestore<R extends FactoryBase<?,?>,V>  {

    private final AttributePathTarget<V> previousPath;
    private final BiConsumer<R,V> setter;
    private final SimpleObjectMapper simpleObjectMapper;

    public PathDataRestore(AttributePathTarget<V> previousPath, BiConsumer<R, V> setter, SimpleObjectMapper simpleObjectMapper) {
        this.previousPath = previousPath;
        this.setter=setter;
        this.simpleObjectMapper = simpleObjectMapper;
    }

    public boolean canMigrate(DataStorageMetadataDictionary previousDataStorageMetadataDictionary, DataJsonNode root){
        return previousPath.isPathToRemovedAttribute(previousDataStorageMetadataDictionary, root);
    }

    public void migrate(DataJsonNode previousRoot, R root) {
        V attributeValue = previousPath.resolveAttributeValue(previousRoot,simpleObjectMapper);
        setter.accept(root,attributeValue);
    }

}
