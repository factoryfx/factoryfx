package io.github.factoryfx.dom.rest;

import com.google.common.base.Strings;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListBaseAttribute;
import io.github.factoryfx.factory.attribute.types.ObjectValueAttribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.factory.metadata.FactoryMetadata;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;

import java.util.*;
import java.util.stream.Collectors;

public class DynamicDataDictionary {
    public HashMap<String,DynamicDataDictionaryItem> classNameToItem = new HashMap<>();


    public DynamicDataDictionary(FactoryBase<?,?> root){
        HashSet<Class<?>> factoryClasses = new HashSet<>();
        for (FactoryBase<?, ?> factoryBase : root.internal().collectChildrenDeep()) {
            factoryClasses.add(factoryBase.getClass());
            factoryBase.internal().visitAttributesMetadata((attributeMetadata) -> {
                if (attributeMetadata.referenceClass!=null){
                    factoryClasses.add(attributeMetadata.referenceClass);
                }
            });
        }

        for (Class<?> factoryClass : factoryClasses) {
            DynamicDataDictionaryItem item = new DynamicDataDictionaryItem();
            classNameToItem.put(factoryClass.getName(), item);

            FactoryMetadata<?, ?> metadata = FactoryMetadataManager.getMetadataUnsafe(factoryClass);
            metadata.visitAttributeMetadata((attributeMetadata) -> {
                DynamicDataDictionaryAttributeItem attributeItem = new DynamicDataDictionaryAttributeItem(getAttributeType(attributeMetadata),
                        !attributeMetadata.required, getLabel(Locale.ENGLISH,attributeMetadata), getLabel(Locale.GERMAN,attributeMetadata),
                        getPossibleEnumValues(attributeMetadata)
                );
                if (!(ObjectValueAttribute.class.isAssignableFrom(attributeMetadata.attributeClass))) {
                    item.attributeNameToItem.put(attributeMetadata.attributeVariableName, attributeItem);
                }
            });
        }

    }

    private String getLabel(Locale locale, AttributeMetadata attributeMetadata) {
        String label = attributeMetadata.labelText.internal_getPreferred(locale);
        if (Strings.isNullOrEmpty(label)) {
            label=attributeMetadata.attributeVariableName;
        }
        return label;
    }

    private String getAttributeType(AttributeMetadata attributeMetadata) {
        if (FactoryBaseAttribute.class.isAssignableFrom(attributeMetadata.attributeClass)){
            return FactoryAttribute.class.getSimpleName();
        }
        if (FactoryListBaseAttribute.class.isAssignableFrom(attributeMetadata.attributeClass)){
            return FactoryListAttribute.class.getSimpleName();
        }
        return attributeMetadata.attributeClass.getSimpleName();
    }

    private List<String> getPossibleEnumValues(AttributeMetadata attributeMetadata) {
        if (attributeMetadata.enumClass!=null){
            return Arrays.stream(attributeMetadata.enumClass.getEnumConstants()).map(Object::toString).collect(Collectors.toList());
        }
        return List.of();
    }

}
