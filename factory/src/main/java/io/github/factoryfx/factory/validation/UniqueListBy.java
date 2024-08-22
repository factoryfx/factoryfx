package io.github.factoryfx.factory.validation;

import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

import io.github.factoryfx.factory.util.LanguageText;

public class UniqueListBy<T, U> implements Validation<List<T>> {

    private final Function<T, U> mapper;
    private final Function<U, LanguageText> errorMessageFunction;

    public UniqueListBy(Function<T, U> mapper) {
        this(mapper, u -> new LanguageText().en("List contains duplicate entry: " + u).de("Liste enthält doppelten Eintrag: " + u));
    }

    public UniqueListBy(Function<T, U> mapper, LanguageText errorMessage) {
        this(mapper, l -> errorMessage);
    }

    public UniqueListBy(Function<T, U> mapper, Function<U, LanguageText> errorMessageFunction) {
        this.mapper = mapper;
        this.errorMessageFunction = errorMessageFunction;
    }

    @Override
    public ValidationResult validate(List<T> list) {
        HashSet<U> set = new HashSet<>();
        for (T item : list) {
            if (!set.add(mapper.apply(item))) {
                return new ValidationResult(true, errorMessageFunction.apply(mapper.apply(item)));
            }
        }
        return ValidationResult.OK;
    }
}
