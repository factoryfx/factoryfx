package io.github.factoryfx.factory.storage.migration.datamigration;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.List;
import java.util.function.Function;

public class AttributeRename<R extends FactoryBase<?,R>,L, F extends FactoryBase<L,R>>  implements DataMigration {
    private final String dataClassNameFullQualified;
    private final String previousAttributeName;
    private String newAttributeName;

    public AttributeRename(Class<F> dataClass, String previousAttributeName, Function<F, Attribute<?,?>> attributeNameProvider) {
        this.dataClassNameFullQualified = dataClass.getName();
        this.previousAttributeName = previousAttributeName;

        F data = FactoryMetadataManager.getMetadata(dataClass).newInstance();
        Attribute<?, ?> newAttribute = attributeNameProvider.apply(data);
        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attribute==newAttribute){
                newAttributeName = attributeVariableName;
            }
        });
        if (this.newAttributeName==null){
            throw new IllegalArgumentException("wrong attributeNameProvider");
        }

    }

    public boolean canMigrate(DataStorageMetadataDictionary dataStorageMetadataDictionary){
        return dataStorageMetadataDictionary.containsClass(dataClassNameFullQualified) &&
               dataStorageMetadataDictionary.containsAttribute(dataClassNameFullQualified,previousAttributeName);
    }

    public void migrate(List<DataJsonNode> dataJsonNodes) {
        dataJsonNodes.stream().filter(dataJsonNode -> dataJsonNode.match(dataClassNameFullQualified)).forEach(dataJsonNode -> {
            dataJsonNode.renameAttribute(previousAttributeName,newAttributeName);
        });
    }

    public void updateDataStorageMetadataDictionary(DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        dataStorageMetadataDictionary.renameAttribute(dataClassNameFullQualified,previousAttributeName,newAttributeName);
    }
}
