package de.factoryfx.server.angularjs.model;

import java.util.Locale;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeTypeInfo;
import de.factoryfx.data.attribute.primitive.DoubleAttribute;
import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;

public class WebGuiAttributeMetadata {
    public final String labelText;
    public final String addonText;
    public final boolean required;
    public final String attributeType;
    public final WebGuiDataType dataType;
    public final WebGuiDataType listItemType;
    public final WebGuiDataType mapValueType;
    public Object listItemEmptyValue;

    public WebGuiAttributeMetadata(Attribute attribute, Locale locale){
        labelText=attribute.getPreferredLabelText(locale);
        addonText=attribute.getPreferredAddonText(locale);
        this.required=attribute.internal_required();
        AttributeTypeInfo attributeType = attribute.internal_getAttributeType();
        this.attributeType = attributeType.attributeType.toString();


        dataType =new WebGuiDataType(attributeType.dataType);
        listItemType =new WebGuiDataType(attributeType.listItemType);

        //TODO remove empty value add im html, instead same logic as in javafx, first edit value than add it
        if (attribute instanceof StringAttribute){
            listItemEmptyValue= "empty";
        }
        if (attribute instanceof IntegerAttribute){
            listItemEmptyValue= 0;
        }
        if (attribute instanceof DoubleAttribute){
            listItemEmptyValue= 0;
        }
        mapValueType=new WebGuiDataType(attributeType.mapValueType);

    }
}
