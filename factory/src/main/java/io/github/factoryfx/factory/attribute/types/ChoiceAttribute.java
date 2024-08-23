package io.github.factoryfx.factory.attribute.types;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.ValidationResult;

public class ChoiceAttribute extends ImmutableValueAttribute<String, ChoiceAttribute> {
    @JsonIgnore
    private Supplier<Collection<String>> possibleValues = Collections::emptyList;

    public ChoiceAttribute() {
        validation(s -> new ValidationResult(s != null && !getPossibleValues().contains(s),
                                             new LanguageText().en("Value not allowed '" + s + "', only " + String.join(", ", getPossibleValues()))
                                                               .de("Ungültiger Wert '" + s + "', erlaubt ist " + String.join(", ", getPossibleValues()))));
    }

    public ChoiceAttribute withPossibleValues(Supplier<Collection<String>> possibleValues) {
        this.possibleValues = possibleValues;
        return this;
    }

    public Collection<String> getPossibleValues() {
        return possibleValues.get();
    }
}
