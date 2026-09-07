package io.github.factoryfx.factory.storage.migration.datamigration;


import com.fasterxml.jackson.databind.JsonNode;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.storage.migration.metadata.AttributeStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * migration for an attribute whose type changed, converting the stored value to the new type with a user supplied converter
 * (instead of dropping the stored data, which is what happens for retyped attributes without a registered migration and no automatic conversion)
 *
 * @param <R> root
 * @param <L> liveobject
 * @param <F> factory containing the attribute
 * @param <VOld> previously stored value type
 * @param <VNew> value type of the new attribute
 */
public class AttributeRetype<R extends FactoryBase<?,R>,L, F extends FactoryBase<L,R>, VOld, VNew>  implements DataMigration {
    private final String dataClassNameFullQualified;
    private String attributeName;
    private String newAttributeClassName;
    private boolean newAttributeIsCollection;
    private final Function<VOld, VNew> converter;
    private final SimpleObjectMapper objectMapper;
    private final BiFunction<DataJsonNode,String,VOld> oldValueParser;

    /**
     * retype migration for a previously stored single value attribute
     * @param dataClass factory class containing the attribute
     * @param previousValueClass previously stored value class
     * @param attributeNameProvider provider for the new attribute, e.g. {@code (f)->f.stringListAttribute}
     * @param converter converts the old value (may be null) to the new value, null result clears the attribute
     * @param objectMapper objectMapper
     */
    public AttributeRetype(Class<F> dataClass, Class<VOld> previousValueClass, Function<F, Attribute<VNew,?>> attributeNameProvider, Function<VOld, VNew> converter, SimpleObjectMapper objectMapper) {
        this(dataClass, attributeNameProvider, converter, objectMapper,
                (dataJsonNode, attributeName) -> dataJsonNode.getAttributeValue(attributeName, previousValueClass, objectMapper));
    }

    /**
     * retype migration for a previously stored list attribute
     * @param dataClass factory class containing the attribute
     * @param previousElementClass previously stored list element class
     * @param attributeNameProvider provider for the new attribute
     * @param converter converts the old list (may be null) to the new value, null result clears the attribute
     * @param objectMapper objectMapper
     * @return migration
     */
    public static <R extends FactoryBase<?,R>,L, F extends FactoryBase<L,R>, VOldElement, VNew> AttributeRetype<R,L,F,List<VOldElement>,VNew> listSource(
            Class<F> dataClass, Class<VOldElement> previousElementClass, Function<F, Attribute<VNew,?>> attributeNameProvider, Function<List<VOldElement>, VNew> converter, SimpleObjectMapper objectMapper) {
        return new AttributeRetype<>(dataClass, attributeNameProvider, converter, objectMapper,
                (dataJsonNode, attributeName) -> {
                    JsonNode value = dataJsonNode.getAttributeValue(attributeName);
                    if (value == null || value.isNull()) {
                        return null;
                    }
                    return objectMapper.treeToValueList(value, previousElementClass);
                });
    }

    private AttributeRetype(Class<F> dataClass, Function<F, Attribute<VNew,?>> attributeNameProvider, Function<VOld, VNew> converter, SimpleObjectMapper objectMapper, BiFunction<DataJsonNode,String,VOld> oldValueParser) {
        this.dataClassNameFullQualified = dataClass.getName();
        this.converter = converter;
        this.objectMapper = objectMapper;
        this.oldValueParser = oldValueParser;

        F data = FactoryMetadataManager.getMetadata(dataClass).newInstance();
        Attribute<?, ?> newAttribute = attributeNameProvider.apply(data);
        data.internal().visitAttributesFlat((attributeMetadata, attribute) -> {
            if (attribute==newAttribute){
                attributeName = attributeMetadata.attributeVariableName;
                newAttributeClassName = attributeMetadata.attributeClass.getName();
                newAttributeIsCollection = Collection.class.isAssignableFrom(attributeMetadata.attributeClass);
            }
        });
        if (this.attributeName ==null){
            throw new IllegalArgumentException("wrong attributeNameProvider");
        }
    }

    public boolean canMigrate(DataStorageMetadataDictionary dataStorageMetadataDictionary){
        if (!dataStorageMetadataDictionary.containsClass(dataClassNameFullQualified) ||
            !dataStorageMetadataDictionary.containsAttribute(dataClassNameFullQualified,attributeName)) {
            return false;
        }
        AttributeStorageMetadata attributeStorageMetadata = dataStorageMetadataDictionary.getAttributeStorageMetadata(dataClassNameFullQualified, attributeName);
        return !newAttributeClassName.equals(attributeStorageMetadata.getAttributeClassName());
    }

    public void migrate(List<DataJsonNode> dataJsonNodes) {
        dataJsonNodes.stream().filter(dataJsonNode -> dataJsonNode.match(dataClassNameFullQualified)).forEach(dataJsonNode -> {
            VOld oldValue = oldValueParser.apply(dataJsonNode, attributeName);
            VNew converted = converter.apply(oldValue);
            JsonNode convertedTree = converted == null ? null : objectMapper.valueToTree(converted);
            dataJsonNode.setAttributeValueTargetShape(attributeName, convertedTree, newAttributeIsCollection);
        });
    }

    public void updateDataStorageMetadataDictionary(DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        dataStorageMetadataDictionary.retypeAttribute(dataClassNameFullQualified, attributeName, newAttributeClassName);
    }
}
