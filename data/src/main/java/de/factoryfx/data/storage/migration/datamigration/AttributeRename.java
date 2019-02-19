package de.factoryfx.data.storage.migration.datamigration;

import de.factoryfx.data.Data;
import de.factoryfx.data.DataDictionary;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.storage.migration.DataMigrationManager;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.List;
import java.util.function.Function;

public class AttributeRename<D extends Data>  implements DataMigrationManager.DataMigration {
    private final String dataClassNameFullQualified;
    private final String previousAttributeName;
    private String newAttributeName;

    public AttributeRename(Class<D> dataClass, String previousAttributeName, Function<D, Attribute<?,?>> attributeNameProvider) {
        this.dataClassNameFullQualified = dataClass.getName();
        this.previousAttributeName = previousAttributeName;

        D data = DataDictionary.getDataDictionary(dataClass).newInstance();
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
}
