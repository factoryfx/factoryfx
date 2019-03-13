package de.factoryfx.data.storage.migration.datamigration;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.function.BiConsumer;

/** restore attribute content from one data class to a new one, based on path**/
public class PathDataRestore<R extends Data,V>  {

    private final AttributePath<V> previousPath;
    private final BiConsumer<R,V> setter;

    public PathDataRestore(AttributePath<V> previousPath, BiConsumer<R, V> setter) {
        this.previousPath = previousPath;
        this.setter=setter;
    }

    public boolean canMigrate(DataStorageMetadataDictionary previousDataStorageMetadataDictionary){
        return previousPath.isPathToRemovedAttribute(previousDataStorageMetadataDictionary);
    }

    public void migrate(DataJsonNode previousRoot, R root) {
        V attributeValue = previousPath.resolve(previousRoot);
        setter.accept(root,attributeValue);
    }
}
