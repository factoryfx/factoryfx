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
    final String name;
    @JsonProperty
    final Class<? extends Attribute> attributeClass;
    @JsonProperty
    Class<?> referenceClass;
    @JsonProperty
    public Class<?> enumClazz;
    @JsonProperty
    Class<?> collectionClazz;
    @JsonProperty
    Class<?> mapKeyType;
    @JsonProperty
    Class<?> mapValueType;


    @JsonProperty
    String en;
    @JsonProperty
    String de;
    @JsonProperty
    String es;
    @JsonProperty
    String fr;
    @JsonProperty
    String it;
    @JsonProperty
    String pt;

    @SuppressWarnings("unchecked")
    public AttributeJsonWrapper(Attribute<?,?> attribute, String name) {
        this.name = name;
        this.attributeClass= attribute.getClass();
        attribute.writeToJsonWrapper(this);
    }

    @SuppressWarnings("unchecked")
    private Object getValue(Attribute<?,?> attribute) {
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
    protected AttributeJsonWrapper(@JsonProperty("value")Object value, @JsonProperty("valueList")List<Data> valueList, @JsonProperty("name")String name,
                                   @JsonProperty("clazz")Class<? extends Attribute> attributeClass, @JsonProperty("referenceClass")Class<?> referenceClass,
                                   @JsonProperty("enumClazz")Class<?> enumClazz,  @JsonProperty("collectionClazz")Class<?> collectionClazz,
                                   @JsonProperty("mapKeyType")Class<?> mapKeyType,  @JsonProperty("mapValueType")Class<?>mapValueType,
                                   @JsonProperty("en")String en, @JsonProperty("de")String de, @JsonProperty("es")String es, @JsonProperty("fr")String fr, @JsonProperty("it")String it, @JsonProperty("pt")String pt) {
        this.value = value;
        this.valueList=valueList;
        this.name = name;
        this.attributeClass=attributeClass;
        this.referenceClass = referenceClass;
        this.enumClazz =enumClazz;
        this.collectionClazz = collectionClazz;
        this.mapKeyType = mapKeyType;
        this.mapValueType = mapValueType;

        this.en=en;
        this.de=de;
        this.es=es;
        this.fr=fr;
        this.it=it;
        this.pt=pt;
    }

    @SuppressWarnings("unchecked")
    public Attribute createAttribute()  {
        return setValue(setMetadataData(instantiateAttribute()));
    }

    @SuppressWarnings("unchecked")
    private Attribute instantiateAttribute()  {
        if (ReferenceAttribute.class.isAssignableFrom(attributeClass)){
            return new DataReferenceAttribute();
        }
        if (ReferenceListAttribute.class.isAssignableFrom(attributeClass)){
            return new DataReferenceListAttribute();
        }
        if (EnumAttribute.class.isAssignableFrom(attributeClass)){
            return new EnumAttribute(enumClazz);
        }
//TODO
//        if (ValueListAttribute.class.isAssignableFrom(attributeClass)){
//            return new ValueListAttribute(collectionClazz);
//        }
//        if (ValueSetAttribute.class.isAssignableFrom(attributeClass)){
//            return new ValueSetAttribute(collectionClazz);
//        }
//        if (ValueMapAttribute.class.isAssignableFrom(attributeClass)){
//            return new ValueMapAttribute(mapKeyType,mapValueType);
//        }

        try {
            return attributeClass.getConstructor().newInstance();
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
        //TODO
//        attribute.metadata.labelText.internal_set(label);
        attribute.readFromJsonWrapper(this);
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

    void patchIds(Data d) {
        if (d==null) return;
        d.setId(UUID.randomUUID().toString());
        d.internal().collectChildrenDeep().forEach(x->x.setId(UUID.randomUUID().toString()));
    }


}
