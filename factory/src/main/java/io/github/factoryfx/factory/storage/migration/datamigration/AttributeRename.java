package io.github.factoryfx.factory.storage.migration.datamigration;


import com.fasterxml.jackson.databind.JsonNode;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.List;
import java.util.function.Function;

public class AttributeRename<R extends FactoryBase<?,R>,L, F extends FactoryBase<L,R>>  implements DataMigration {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AttributeRename.class);

    private final String dataClassNameFullQualified;
    private final String previousAttributeName;
    private String newAttributeName;

    public AttributeRename(Class<F> dataClass, String previousAttributeName, Function<F, Attribute<?,?>> attributeNameProvider) {
        this.dataClassNameFullQualified = dataClass.getName();
        this.previousAttributeName = previousAttributeName;

        F data = FactoryMetadataManager.getMetadata(dataClass).newInstance();
        Attribute<?, ?> newAttribute = attributeNameProvider.apply(data);
        data.internal().visitAttributesFlat((attributeMetadata, attribute) -> {
            if (attribute==newAttribute){
                newAttributeName = attributeMetadata.attributeVariableName;
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
            //old and new attribute can coexist in stored data when both existed during a transition (deprecated old
            //attribute kept). Never clobber an existing value of the target attribute: keep it and drop the old one.
            if (hasValue(dataJsonNode, newAttributeName)) {
                if (hasValue(dataJsonNode, previousAttributeName)) {
                    logger.warn("rename migration {}: '{}' not renamed to '{}' because the target attribute already has a value, the stored value of '{}' is dropped", dataClassNameFullQualified, previousAttributeName, newAttributeName, previousAttributeName);
                }
                dataJsonNode.removeAttribute(previousAttributeName);
            } else {
                dataJsonNode.renameAttribute(previousAttributeName,newAttributeName);
            }
        });
    }

    private boolean hasValue(DataJsonNode dataJsonNode, String attributeName) {
        JsonNode value = dataJsonNode.getAttributeValue(attributeName);
        if (value == null || value.isNull()) {
            return false;
        }
        return !(value.isArray() && value.isEmpty());
    }

    public void updateDataStorageMetadataDictionary(DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        dataStorageMetadataDictionary.renameAttribute(dataClassNameFullQualified,previousAttributeName,newAttributeName);
    }
}
