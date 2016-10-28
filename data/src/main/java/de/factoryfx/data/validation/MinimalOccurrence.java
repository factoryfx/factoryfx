package de.factoryfx.data.validation;

import de.factoryfx.data.util.LanguageText;

import java.util.List;

public class MinimalOccurrence<T,R extends List<T>> implements Validation<R> {

    private final int minimalOccurence;

    public MinimalOccurrence(int minimalOccurence) {
        this.minimalOccurence = minimalOccurence;
    }

    @Override
    public LanguageText getValidationDescription() {
        return new LanguageText().en("at least "+minimalOccurence+" item(s) required").de("Mindestens "+minimalOccurence+" erforderlich");
    }

    @Override
    public boolean validate(R value) {
        return value != null && value.size() >= minimalOccurence;
    }

}
