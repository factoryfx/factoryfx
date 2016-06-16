package de.factoryfx.development.angularjs.server;

import java.util.Locale;

import de.factoryfx.factory.attribute.AttributeMetadata;

public class WebGuiAttributeMetadata {
    public String labelText;

    public WebGuiAttributeMetadata(AttributeMetadata attributeMetadata, Locale locale){
        labelText=attributeMetadata.labelText.get(locale);
    }
}
