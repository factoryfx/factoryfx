package de.factoryfx.development.angularjs.model;

import java.util.Locale;

import de.factoryfx.factory.attribute.Attribute;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.AttributeTypeInfo;
import de.factoryfx.factory.validation.ObjectRequired;
import de.factoryfx.factory.validation.Validation;

public class WebGuiAttributeMetadata {
    public final String labelText;
    public final boolean required;
    public final String attributeType;
    public final WebGuiDataType dataType;
    public final WebGuiDataType listItemType;
    public final WebGuiDataType mapValueType;
    public final Object listItemEmptyValue;

    public WebGuiAttributeMetadata(AttributeMetadata attributeMetadata, Locale locale, Attribute<?, ?> attribute){
        labelText=attributeMetadata.labelText.getPreferred(locale);
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
