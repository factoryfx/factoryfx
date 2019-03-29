package io.github.factoryfx.factory.validation;

import java.util.Locale;
import java.util.function.Function;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.util.LanguageText;

public class ValidationError {
    private final LanguageText validationDescription;
    private final Attribute<?,?> attribute;
    private final FactoryBase<?,?> parent;
    private final String attributeVariableName;

    public ValidationError(LanguageText validationDescription, Attribute<?,?> attribute, FactoryBase<?,?> parent, String attributeVariableName) {
        this.validationDescription = validationDescription;
        this.attribute = attribute;
        this.parent = parent;
        this.attributeVariableName= attributeVariableName;
    }

    public String validationDescription(Function<LanguageText,String> languageTextEvaluator){
        return languageTextEvaluator.apply(validationDescription);
    }

    public String attributeDescription(Locale locale){
        String label = attribute.internal_getPreferredLabelText(locale);
        if (label.isEmpty()){
            label=attributeVariableName;
        }
        return label;
    }


    public String getSimpleErrorDescription(){
        return "        Factory class: "+parent.getClass().getName()+
                "\n        Attribute: "+attributeVariableName+" (label: "+attribute.internal_getPreferredLabelText(Locale.ENGLISH)+ ")" +
                "\n        Error: "+validationDescription.internal_getPreferred(Locale.ENGLISH);
    }
}
