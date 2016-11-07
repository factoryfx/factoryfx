package de.factoryfx.server.angularjs.model;

import java.util.Locale;

import de.factoryfx.data.Data;
import de.factoryfx.data.validation.ValidationError;

public class WebGuiValidationError {

    public final String validationDescription;
    public final String attributeLabel;
    public final String factoryId;
    public final String factoryDisplayText;

    public WebGuiValidationError(ValidationError validationError, Locale locale, Data factoryBase){
        validationDescription=validationError.validationDescription.getPreferred(locale);
        attributeLabel=validationError.attributeLabel.getPreferred(locale);
        factoryId=factoryBase.getId().toString();
        factoryDisplayText=factoryBase.internal().getDisplayText();

    }
}
