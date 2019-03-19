package io.github.factoryfx.data.validation;

import java.util.Locale;
import java.util.function.Function;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.Attribute;
import io.github.factoryfx.data.util.LanguageText;

public class ValidationError {
    private final LanguageText validationDescription;
    private final Attribute<?,?> attribute;
    private final Data parent;
    private final String attributeVariableName;

    public ValidationError(LanguageText validationDescription, Attribute<?,?> attribute, Data parent, String attributeVariableName) {
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
