package de.factoryfx.data.validation;

import de.factoryfx.data.util.LanguageText;

import java.util.List;

public class MinimalOccurrence<T,R extends List<T>> implements Validation<R> {

    private final int minimalOccurence;

    public MinimalOccurrence(int minimalOccurence) {
        this.minimalOccurence = minimalOccurence;
    }

    @Override
    public ValidationResult validate(R value) {
        boolean error = false;
        if (value != null){
            error = value.size() < minimalOccurence;
        }
        return new ValidationResult(error,new LanguageText().en("at least "+minimalOccurence+" item(s) required").de("Mindestens "+minimalOccurence+" erforderlich"));
    }

}
