package de.factoryfx.development.angularjs.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.factoryfx.factory.attribute.Attribute;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.EnumAttribute;
import de.factoryfx.factory.validation.ObjectRequired;

public class WebGuiAttributeMetadata {
    public String labelText;
    public String type;
    public boolean required;
    public List<String> enumValues;

    public WebGuiAttributeMetadata(AttributeMetadata attributeMetadata, Locale locale, Attribute<?, ?> attribute){
        labelText=attributeMetadata.labelText.getPreferred(locale);
        type=attribute.getClass().getSimpleName();

        required=false;
        attribute.validations.forEach(validation -> {
            if (validation instanceof ObjectRequired<?>) {
                required = true;
            }
        });

        if (attribute instanceof EnumAttribute){
            enumValues = new ArrayList<>();
            for (Object item: ((EnumAttribute) attribute).getEnumValues()){
                enumValues.add(item.toString());
            }

        }
    }
}
