package de.factoryfx.development.angularjs.server.model;

import java.util.Locale;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.validation.ValidationError;

public class WebGuiValidationError {

    public final String validationDescription;
    public final String attributeLabel;
    public final String factoryId;
    public final String factoryDisplayText;

    public WebGuiValidationError(ValidationError validationError, Locale locale, FactoryBase<?,?> factoryBase){
        validationDescription=validationError.validationDescription.getPreferred(locale);
        attributeLabel=validationError.attributeLabel.getPreferred(locale);
        factoryId=factoryBase.getId();
        factoryDisplayText=factoryBase.getDisplayText();

    }
}
