package de.factoryfx.data.attribute;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sun.javafx.collections.ObservableListWrapper;
import de.factoryfx.data.AttributeAndName;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.util.LanguageText;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

/** wraps attribute so its serializable/deserializable in json
 *  used e.g for dynamic attributes*/
public class AttributeJsonWrapper {
    @JsonProperty
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@clazz")
    Object value;
    @JsonProperty
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@clazz")
    List<Data> valueList;//special case for referencelists
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
        this.value = getValue(attribute);
        this.label = attribute.metadata.labelText;
        this.name = name;
        this.attributeClass= attribute.getClass();
        if (attribute instanceof ReferenceAttribute){
            referenceClass = ((ReferenceAttribute)attribute).internal_getReferenceClass();
        }
        if (this.value instanceof Data) {
            this.value = ((Data) this.value).internal().copy();
            patchIds((Data)this.value);
        }
        if (attribute instanceof ReferenceListAttribute){
            referenceClass = ((ReferenceListAttribute)attribute).internal_getReferenceClass();
            value=null;
            valueList=new ArrayList<>(((ReferenceListAttribute)attribute).get());
            valueList.replaceAll(d->{
                Data theCopy = d.internal().copy();
                patchIds(theCopy);
                return theCopy;
            });
        }
        if (attribute instanceof EnumAttribute){
            enumClazz = ((EnumAttribute)attribute).internal_getEnumClass();
        }
    }

    @SuppressWarnings("unchecked")
    private Object getValue(Attribute<?> attribute) {
        if (valueList!=null){
            return valueList;
        }

        //internal_copy() as workaround for mutable values like list
        Object value = attribute.internal_copy().get();
        if (value instanceof  ObservableList){
            return new ArrayList<>((ObservableList)value);
        }
        if (value instanceof ObservableSet){
            return new HashSet<>((ObservableSet)value);
        }
        if (value instanceof ObservableMap){
            return new HashMap<>((ObservableMap)value);
        }
        return value;
    }

    @JsonCreator
    protected AttributeJsonWrapper(@JsonProperty("value")Object value, @JsonProperty("valueList")List<Data> valueList, @JsonProperty("label")LanguageText label, @JsonProperty("name")String name,
                                   @JsonProperty("clazz")Class<? extends Attribute> attributeClass, @JsonProperty("referenceClass")Class<?> referenceClass,
                                   @JsonProperty("enumClazz")Class<?> enumClazz,  @JsonProperty("collectionClazz")Class<?> collectionClazz,
                                   @JsonProperty("mapKeyType")Class<?> mapKeyType,  @JsonProperty("mapValueType")Class<?>mapValueType) {
        this.value = value;
        this.valueList=valueList;
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
            return attributeClass.getConstructor(AttributeMetadata.class).newInstance(new AttributeMetadata());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Attribute setValue(Attribute attribute){
        if (valueList !=null){
            attribute.set(valueList);
            return attribute;
        }
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

    @JsonIgnore
    public Optional<List<Data>> valueList() {
        return Optional.ofNullable(valueList);
    }

    private void patchIds(Data d) {
        d.setId(UUID.randomUUID().toString());
        d.internal().collectChildrenDeep().forEach(x->x.setId(UUID.randomUUID().toString()));
    }


}
