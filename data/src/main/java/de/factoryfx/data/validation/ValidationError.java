package de.factoryfx.data.validation;

import java.util.function.Function;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.util.LanguageText;

public class ValidationError {
    private final LanguageText validationDescription;
    private final Attribute<?> attribute;
    private final Data parent;

    public ValidationError(LanguageText validationDescription, Attribute<?> attribute, Data parent) {
        this.validationDescription = validationDescription;
        this.attribute = attribute;
        this.parent = parent;
    }

    public boolean isErrorFor(Attribute<?> attribute){
        return this.attribute==attribute;
    }

    public String validationDescription(Function<LanguageText,String> languageTextEvaluator){
        return languageTextEvaluator.apply(validationDescription);
    }

    public String validationDescriptionForChild(Function<LanguageText,String> languageTextEvaluator){
        return parent.internal().getDisplayText()+" | "+ attribute.getPreferredLabelText(languageTextEvaluator)+" | "+languageTextEvaluator.apply(validationDescription);
    }

    public String attributeDescription(Function<LanguageText,String> languageTextEvaluator){
        return attribute.getPreferredLabelText(languageTextEvaluator);
    }

}
