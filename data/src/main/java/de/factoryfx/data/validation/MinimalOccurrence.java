package de.factoryfx.data.validation;

import de.factoryfx.data.util.LanguageText;

import java.util.List;

public class MinimalOccurrence<T,R extends List<T>> implements Validation<R> {

    private final int minimalOccurrence;

    public MinimalOccurrence(int minimalOccurrence) {
        this.minimalOccurrence = minimalOccurrence;
    }

    @Override
    public ValidationResult validate(R value) {
        boolean error = false;
        if (value != null){
            error = value.size() < minimalOccurrence;
        }
        return new ValidationResult(error,new LanguageText().en("at least "+ minimalOccurrence +" item(s) required").de("Mindestens "+ minimalOccurrence +" erforderlich"));
    }

}
