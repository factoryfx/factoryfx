package de.factoryfx.data.attribute;

import java.lang.reflect.Constructor;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.factoryfx.data.AttributeAndName;
import de.factoryfx.data.Data;

/** wraps attribute so its serializable/deserializable in json
 *  used e.g for dynamic attributes*/
public class AttributeJsonWrapper {
    @JsonProperty
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@clazz")
    public Object value;
    @JsonProperty
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@clazz")
    public List<Data> valueList;//special case for referencelists
    @JsonProperty
    public final String name;
    @JsonProperty
    public final Class<? extends Attribute> attributeClass;
    @JsonProperty
    Class<?> referenceClass;
    @JsonProperty
    public Class<?> enumClazz;
    @JsonProperty
    public Class<?> collectionClazz;
    @JsonProperty
    public Class<?> mapKeyType;
    @JsonProperty
    public Class<?> mapValueType;


    @JsonProperty
    public String en;
    @JsonProperty
    public String de;
    @JsonProperty
    public String es;
    @JsonProperty
    public String fr;
    @JsonProperty
    public String it;
    @JsonProperty
    public String pt;

    @JsonProperty
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@clazz")
    public Object various;

    @SuppressWarnings("unchecked")
    public AttributeJsonWrapper(Attribute<?,?> attribute, String name) {
        this.name = name;
        this.attributeClass= attribute.getClass();
        attribute.internal_writeToJsonWrapper(this);
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
        return instantiateAttribute();
    }

    @SuppressWarnings("unchecked")
    private Attribute instantiateAttribute()  {
        try {
            Constructor<?> constructor= attributeClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Attribute attribute = (Attribute) constructor.newInstance();
            attribute.internal_readFromJsonWrapper(this);
            return attribute;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AttributeAndName createAttributeAndName(){
        return new AttributeAndName(createAttribute(),name);
    }
    @JsonIgnore
    public String getDisplayText(){
        try {
            return createAttribute().getDisplayText();
        } catch (ClassCastException ce) {
            return "nicht verfügbar";
        }
    }

    void patchIds(Data d) {
        if (d==null) return;
        d.setId(UUID.randomUUID().toString());
        d.internal().collectChildrenDeep().forEach(x->x.setId(UUID.randomUUID().toString()));
    }


}
