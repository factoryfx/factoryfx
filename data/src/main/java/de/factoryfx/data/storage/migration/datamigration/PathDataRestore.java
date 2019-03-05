package de.factoryfx.data.storage.migration.datamigration;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.function.BiConsumer;

/** restore attribute content from one data class to a new one, base don path**/
public class PathDataRestore<R extends Data,V>  {

    private final AttributePath<V> previousPath;

    private final BiConsumer<R,V> setter;
    private final Class<V> valueClass;

    public PathDataRestore(AttributePath previousPath, Class<V> valueClass, BiConsumer<R, V> setter) {
        this.previousPath = previousPath;
        this.setter=setter;
        this.valueClass=valueClass;
    }

    public boolean canMigrate(DataStorageMetadataDictionary previousDataStorageMetadataDictionary, DataStorageMetadataDictionary currentDataStorageMetadataDictionary){
//        return currentDataStorageMetadataDictionary.isRemovedAttribute(singletonPreviousDataClass, previousAttributeName) &&
//               previousDataStorageMetadataDictionary.isSingleton(singletonPreviousDataClass) &&
//               previousDataStorageMetadataDictionary.containsClass(singletonPreviousDataClass) &&
//               previousDataStorageMetadataDictionary.containsAttribute(singletonPreviousDataClass,previousAttributeName);
        return true;
    }

    public void migrate(DataJsonNode previousRoot, R root) {
        V attributeValue = previousPath.resolve(previousRoot);

        setter.accept(root,attributeValue);
    }
}
