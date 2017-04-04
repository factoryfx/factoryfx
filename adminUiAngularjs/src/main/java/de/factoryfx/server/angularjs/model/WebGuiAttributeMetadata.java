package de.factoryfx.server.angularjs.model;

import java.util.Locale;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.AttributeTypeInfo;

public class WebGuiAttributeMetadata {
    public final String labelText;
    public final String addonText;
    public final boolean required;
    public final String attributeType;
    public final WebGuiDataType dataType;
    public final WebGuiDataType listItemType;
    public final WebGuiDataType mapValueType;
    public final Object listItemEmptyValue;

    public WebGuiAttributeMetadata(AttributeMetadata attributeMetadata, Locale locale, Attribute<?> attribute){
        labelText=attributeMetadata.labelText.internal_getPreferred(locale);
        addonText=attributeMetadata.addonText;
        this.required=attribute.internal_required();
        AttributeTypeInfo attributeType = attribute.internal_getAttributeType();
        this.attributeType = attributeType.attributeType.toString();


        dataType =new WebGuiDataType(attributeType.dataType);
        listItemType =new WebGuiDataType(attributeType.listItemType);
        listItemEmptyValue=attributeType.listItemEmptyValue;
        mapValueType=new WebGuiDataType(attributeType.mapValueType);

    }
}
