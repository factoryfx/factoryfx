package de.factoryfx.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.sun.javafx.collections.ObservableListWrapper;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

public class DynamicData extends Data{
    @JsonIgnore
    List<AttributeAndName> dynamicDataAttributeAndNames=new ArrayList<>();

    @Override
    List<AttributeAndName> getAttributes(){
        final ArrayList<AttributeAndName> attributeAndNames = new ArrayList<>(super.getAttributes());
        attributeAndNames.addAll(dynamicDataAttributeAndNames);
        return attributeAndNames;
    }

    @Override
    Data newInstance() {
        try {
            final DynamicData result = ((DynamicData)super.newInstance());

            for (AttributeAndName attributeAndName: dynamicDataAttributeAndNames){
                final Attribute attribute = createAttribute(attributeAndName.attribute.getClass());
                attribute.metadata.labelText.internal_set(attributeAndName.attribute.metadata.labelText);
                result.dynamicDataAttributeAndNames.add(new AttributeAndName(attribute,attributeAndName.name));
            }

            return result;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private Attribute createAttribute(Class<? extends Attribute> clazz)  {
        try {
            return clazz.getConstructor(AttributeMetadata.class).newInstance(new AttributeMetadata());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @JsonProperty
    public List<DynamicDataAttribute> getDynamicDataAttributes(){
        final ArrayList<DynamicDataAttribute> result = new ArrayList<>();
        for (AttributeAndName attributeAndName: dynamicDataAttributeAndNames){
            Object value = attributeAndName.attribute.get();
            if (value instanceof ObservableListWrapper){
                value=new ArrayList<>((ObservableListWrapper)value);
            }
            result.add(new DynamicDataAttribute(value,attributeAndName.attribute.getClass(),attributeAndName.attribute.metadata.labelText,attributeAndName.name));
        }
        return result;
    }

    @JsonProperty
    public void setDynamicDataAttributes(List<DynamicDataAttribute> dynamicDataAttributes){
        for (DynamicDataAttribute dynamicDataAttribute: dynamicDataAttributes){
            final Attribute attribute = createAttribute(dynamicDataAttribute.attributeClass);
            attribute.set(dynamicDataAttribute.value);
            attribute.metadata.labelText.internal_set(dynamicDataAttribute.label);
            dynamicDataAttributeAndNames.add(new AttributeAndName(attribute,dynamicDataAttribute.name));
        }
    }


    public void addAttribute(ValueAttribute<?> attribute){
        dynamicDataAttributeAndNames.add(new AttributeAndName(attribute, toIdentifier(attribute.metadata.labelText.internal_getPreferred(Locale.ENGLISH))));
    }

    public String toIdentifier(String value) {//TODO for js?
        if (Strings.isNullOrEmpty(value)) {
            return UUID.randomUUID().toString();
        }
        StringBuilder result = new StringBuilder();
        if(!Character.isJavaIdentifierStart(value.charAt(0))) {
            result.append("_");
        }
        for (char c : value.toCharArray()) {
            if(!Character.isJavaIdentifierPart(c)) {
                result.append("_");
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

}
