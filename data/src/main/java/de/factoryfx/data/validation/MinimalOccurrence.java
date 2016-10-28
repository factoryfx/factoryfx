package de.factoryfx.data.validation;

import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.util.LanguageText;

public class MinimalOccurrence<T extends ReferenceListAttribute> implements Validation<T> {

    private final int minimalOccurence;

    public MinimalOccurrence(int minimalOccurence) {
        this.minimalOccurence = minimalOccurence;
    }

    @Override
    public LanguageText getValidationDescription() {
        return new LanguageText().en("at least "+minimalOccurence+" item(s) required").de("Mindestens "+minimalOccurence+" erforderlich");
    }

    @Override
    public boolean validate(T value) {
        return value != null && value.get().size() >= minimalOccurence;
    }
}
