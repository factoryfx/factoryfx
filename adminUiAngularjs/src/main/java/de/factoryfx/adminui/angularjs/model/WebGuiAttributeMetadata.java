package de.factoryfx.adminui.angularjs.model;

import java.util.Locale;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.AttributeTypeInfo;
import de.factoryfx.data.validation.ObjectRequired;
import de.factoryfx.data.validation.Validation;

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
        labelText=attributeMetadata.labelText.getPreferred(locale);
        addonText=attributeMetadata.addonText;
        boolean required=false;
        for (Validation<?> validation: attribute.validations){
            if (validation instanceof ObjectRequired<?>) {
                required = true;
                break;
            }
        }
        this.required=required;
        AttributeTypeInfo attributeType = attribute.getAttributeType();
        this.attributeType = attributeType.attributeType.toString();


        dataType =new WebGuiDataType(attributeType.dataType);
        listItemType =new WebGuiDataType(attributeType.listItemType);
        listItemEmptyValue=attributeType.listItemEmptyValue;
        mapValueType=new WebGuiDataType(attributeType.mapValueType);

    }
}
