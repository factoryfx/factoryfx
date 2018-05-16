package de.factoryfx.data.validation;

import java.util.Locale;
import java.util.function.Function;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.util.LanguageText;

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

    public boolean isErrorFor(Attribute<?,?> attribute){
        return this.attribute==attribute;
    }

    public String validationDescription(Function<LanguageText,String> languageTextEvaluator){
        return languageTextEvaluator.apply(validationDescription);
    }

    public String validationDescriptionForChild(Locale locale){
        return parent.internal().getDisplayText()+" | "+ attributeDescription(locale)+" | "+validationDescription.internal_getPreferred(locale);
    }

    public String attributeDescription(Locale locale){
        String label = attribute.internal_getPreferredLabelText(locale);
        if (label.isEmpty()){
            label=attributeVariableName;
        }
        return label;
    }


    public String getSimpleErrorDescription(){
        return "        Factory class: "+parent.getClass()+
                "\n        Attribute: "+attributeDescription(Locale.ENGLISH)+
                "\n        Error: "+validationDescription.internal_getPreferred(Locale.ENGLISH);
    }
}
