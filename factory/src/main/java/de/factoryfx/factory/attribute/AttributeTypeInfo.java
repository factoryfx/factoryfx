package de.factoryfx.factory.attribute;

public class AttributeTypeInfo {
    public final Class<?> dataType;
    public final Class<?> mapKeyType;
    public final Class<?> mapValueType;
    public final AttributeTypeCategory attributeType;
    public final Class<?> listItemType;
    public final Object listItemEmptyValue;

    public AttributeTypeInfo(Class<?> dataType, Class<?> mapKeyType, Class<?> mapValueType, AttributeTypeCategory attributeType) {
        this(dataType,mapKeyType,mapValueType,null,attributeType,null);
    }

    public AttributeTypeInfo(Class<?> dataType, Class<?> mapKeyType, Class<?> mapValueType, Class<?> listItemType, AttributeTypeCategory attributeType, Object listItemEmptyValue) {
        this.dataType = dataType;
        this.mapKeyType = mapKeyType;
        this.mapValueType = mapValueType;
        this.listItemType = listItemType;
        this.attributeType=attributeType;
        this.listItemEmptyValue=listItemEmptyValue;
    }

    public AttributeTypeInfo(Class<?> dataType) {
        this(dataType,null,null,null,AttributeTypeCategory.VALUE,null);
    }

    public static enum AttributeTypeCategory{
        VALUE,MAP,COLLECTION,REFERENCE,REFERENCE_LIST
    }




}
