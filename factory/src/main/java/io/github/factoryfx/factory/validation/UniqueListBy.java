package io.github.factoryfx.factory.validation;

import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

import io.github.factoryfx.factory.util.LanguageText;

public class UniqueListBy<T, V> implements Validation<List<T>> {

    private final Function<T, V> mapper;
    private final Function<List<T>, LanguageText> errorMessageFunction;

    public UniqueListBy(Function<T, V> mapper) {
        this(mapper, l -> new LanguageText().en("List contains duplicate entries").de("Liste enthält doppelte Einträge"));
    }

    public UniqueListBy(Function<T, V> mapper, LanguageText errorMessage) {
        this(mapper, l -> errorMessage);
    }

    public UniqueListBy(Function<T, V> mapper, Function<List<T>, LanguageText> errorMessageFunction) {
        this.mapper = mapper;
        this.errorMessageFunction = errorMessageFunction;
    }

    @Override
    public ValidationResult validate(List<T> list) {
        HashSet<V> set = new HashSet<>();
        for (T item : list) {
            if (!set.add(mapper.apply(item))) {
                return new ValidationResult(true, errorMessageFunction.apply(list));
            }
        }
        return ValidationResult.OK;
    }
}
