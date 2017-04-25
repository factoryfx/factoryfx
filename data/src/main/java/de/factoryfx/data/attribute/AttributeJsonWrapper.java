package de.factoryfx.data.attribute;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.javafx.collections.ObservableListWrapper;
import de.factoryfx.data.AttributeAndName;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.util.LanguageText;

/** wraps attribute so its serializable/deserializable in json
 *  used e.g for dynamic attributes*/
public class AttributeJsonWrapper {
    @JsonProperty
    Object value;
    @JsonProperty
    final LanguageText label;
    @JsonProperty
    final String name;
    @JsonProperty
    final Class<? extends Attribute> attributeClass;
    @JsonProperty
    Class<?> referenceClass;
    @JsonProperty
    Class<?> enumClazz;
    @JsonProperty
    Class<?> collectionClazz;
    @JsonProperty
    Class<?> mapKeyType;
    @JsonProperty
    Class<?> mapValueType;

    @SuppressWarnings("unchecked")
    public AttributeJsonWrapper(Attribute<?> attribute, String name) {
        this.value = attribute.internal_copy().get(); //internal_copy() as workaround for mutable values like list
        this.label = attribute.metadata.labelText;
        this.name = name;
        this.attributeClass= attribute.getClass();
        if (attribute instanceof ReferenceAttribute){
            referenceClass = ((ReferenceAttribute)attribute).internal_getReferenceClass();
        }
        if (attribute instanceof ReferenceListAttribute){
            referenceClass = ((ReferenceListAttribute)attribute).internal_getReferenceClass();
        }
        if (attribute instanceof EnumAttribute){
            enumClazz = ((EnumAttribute)attribute).internal_getEnumClass();
        }
    }

    @JsonCreator
    protected AttributeJsonWrapper(@JsonProperty("value")Object value, @JsonProperty("label")LanguageText label, @JsonProperty("name")String name,
                                   @JsonProperty("clazz")Class<? extends Attribute> attributeClass, @JsonProperty("referenceClass")Class<?> referenceClass,
                                   @JsonProperty("enumClazz")Class<?> enumClazz,  @JsonProperty("collectionClazz")Class<?> collectionClazz,
                                   @JsonProperty("mapKeyType")Class<?> mapKeyType,  @JsonProperty("mapValueType")Class<?>mapValueType) {
        this.value = value;
        this.label = label;
        this.name = name;
        this.attributeClass=attributeClass;
        this.referenceClass = referenceClass;
        this.enumClazz =enumClazz;
        this.collectionClazz = collectionClazz;
        this.mapKeyType = mapKeyType;
        this.mapValueType = mapValueType;
    }

    @SuppressWarnings("unchecked")
    public Attribute createAttribute()  {
        return setValue(setMetadataData(instantiateAttribute()));
    }

    @SuppressWarnings("unchecked")
    private Attribute instantiateAttribute()  {
        if (ReferenceAttribute.class.isAssignableFrom(attributeClass)){
            return new ReferenceAttribute(new AttributeMetadata(),referenceClass);
        }
        if (ReferenceListAttribute.class.isAssignableFrom(attributeClass)){
            return new ReferenceListAttribute(new AttributeMetadata(),referenceClass);
        }
        if (EnumAttribute.class.isAssignableFrom(attributeClass)){
            return new EnumAttribute(enumClazz,new AttributeMetadata());
        }
        if (ValueListAttribute.class.isAssignableFrom(attributeClass)){
            return new ValueListAttribute(collectionClazz,new AttributeMetadata());
        }
        if (ValueSetAttribute.class.isAssignableFrom(attributeClass)){
            return new ValueSetAttribute(collectionClazz,new AttributeMetadata());
        }
        if (ValueMapAttribute.class.isAssignableFrom(attributeClass)){
            return new ValueMapAttribute(new AttributeMetadata(),mapKeyType,mapValueType);
        }

        try {
            final Attribute attribute = attributeClass.getConstructor(AttributeMetadata.class).newInstance(new AttributeMetadata());
            return attribute;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Attribute setValue(Attribute attribute){
        if (value instanceof ObservableListWrapper){
            value=new ArrayList<>((ObservableListWrapper)value);
        }
        attribute.set(value);
        return attribute;
    }

    private Attribute setMetadataData(Attribute attribute){
        attribute.metadata.labelText.internal_set(label);
        return attribute;
    }

    public AttributeAndName createAttributeAndName(){
        return new AttributeAndName(createAttribute(),name);
    }
    @JsonIgnore
    public String getDisplayText(){
        return createAttribute().getDisplayText();
    }
}
