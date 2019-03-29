package io.github.factoryfx.factory.storage.migration.datamigration;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.List;
import java.util.function.BiConsumer;

/** restore attribute content from one data class to a new one, both  are singletons**/
public class SingletonDataRestore<R extends FactoryBase<?,?>,V>  {

    private final String previousAttributeName;
    private String singletonPreviousDataClass;

    private final BiConsumer<R,V> setter;
    private final Class<V> valueClass;
    private final SimpleObjectMapper simpleObjectMapper;

    public SingletonDataRestore(String singletonPreviousDataClass, String previousAttributeName, Class<V> valueClass, BiConsumer<R,V> setter, SimpleObjectMapper simpleObjectMapper) {
        this.previousAttributeName = previousAttributeName;
        this.singletonPreviousDataClass=singletonPreviousDataClass;
        this.setter=setter;
        this.valueClass=valueClass;
        this.simpleObjectMapper = simpleObjectMapper;
    }

    public boolean canMigrate(DataStorageMetadataDictionary previousDataStorageMetadataDictionary){
        return previousDataStorageMetadataDictionary.isRemovedAttribute(singletonPreviousDataClass, previousAttributeName) &&
               previousDataStorageMetadataDictionary.isSingleton(singletonPreviousDataClass) &&
               previousDataStorageMetadataDictionary.containsClass(singletonPreviousDataClass) &&
               previousDataStorageMetadataDictionary.containsAttribute(singletonPreviousDataClass,previousAttributeName);
    }

    public void migrate(List<DataJsonNode> dataJsonNodes, R root) {
        DataJsonNode previousData = dataJsonNodes.stream().filter(dataJsonNode -> dataJsonNode.match(singletonPreviousDataClass)).findFirst().get();
        V attributeValue = previousData.getAttributeValue(previousAttributeName, valueClass, simpleObjectMapper);

        setter.accept(root,attributeValue);
    }
}
