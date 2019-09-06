package io.github.factoryfx.dom.rest;

import com.google.common.base.Strings;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListBaseAttribute;
import io.github.factoryfx.factory.attribute.types.EnumAttribute;
import io.github.factoryfx.factory.attribute.types.EnumListAttribute;
import io.github.factoryfx.factory.attribute.types.ObjectValueAttribute;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DynamicDataDictionary {
    public HashMap<String,DynamicDataDictionaryItem> classNameToItem = new HashMap<>();


    public DynamicDataDictionary(FactoryBase<?,?> root){
        HashSet<Class<?>> factoryClasses = new HashSet<>();
        for (FactoryBase<?, ?> factoryBase : root.internal().collectChildrenDeep()) {
            factoryClasses.add(factoryBase.getClass());
            factoryBase.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                if (attribute instanceof FactoryAttribute){
                    factoryClasses.add(((FactoryAttribute<?,?>)attribute).internal_getReferenceClass());
                }
                if (attribute instanceof FactoryListAttribute){
                    factoryClasses.add(((FactoryListAttribute<?,?>)attribute).internal_getReferenceClass());
                }
            });
        }

        for (Class<?> factoryClass : factoryClasses) {
            DynamicDataDictionaryItem item = new DynamicDataDictionaryItem();
            classNameToItem.put(factoryClass.getName(), item);

            FactoryBase factoryBase = FactoryMetadataManager.getMetadataUnsafe(factoryClass).newInstance();
            factoryBase.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                DynamicDataDictionaryAttributeItem attributeItem = new DynamicDataDictionaryAttributeItem(getAttributeType(attribute),
                        !attribute.internal_required(), getLabel(Locale.ENGLISH,attribute,attributeVariableName), getLabel(Locale.GERMAN,attribute,attributeVariableName),
                        getPossibleEnumValues(attribute)
                );
                if (!(attribute instanceof ObjectValueAttribute<?>)) {
                    item.attributeNameToItem.put(attributeVariableName, attributeItem);
                }
            });
        }

    }

    private String getLabel(Locale locale, Attribute<?, ?> attribute, String attributeVariableName) {
        String label = attribute.internal_getPreferredLabelText(locale);
        if (Strings.isNullOrEmpty(label)) {
            label=attributeVariableName;
        }
        return label;
    }

    private String getAttributeType(Attribute<?, ?> attribute) {
        if (attribute instanceof FactoryBaseAttribute){
            return FactoryAttribute.class.getSimpleName();
        }
        if (attribute instanceof FactoryListBaseAttribute){
            return FactoryListAttribute.class.getSimpleName();
        }
        return attribute.getClass().getSimpleName();
    }

    private List<String> getPossibleEnumValues(Attribute<?, ?> attribute) {
        if (attribute instanceof EnumAttribute){
            return ((EnumAttribute<?>)attribute).internal_possibleEnumValues().stream().map(Enum::toString).collect(Collectors.toList());
        }
        if (attribute instanceof EnumListAttribute){
            return ((EnumListAttribute<?>)attribute).internal_possibleEnumValues().stream().map(Enum::toString).collect(Collectors.toList());
        }
        return List.of();
    }

}
