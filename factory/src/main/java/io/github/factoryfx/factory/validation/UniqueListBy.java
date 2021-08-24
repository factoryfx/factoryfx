package io.github.factoryfx.factory.validation;

import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

import io.github.factoryfx.factory.util.LanguageText;

public class UniqueListBy<T, V> implements Validation<List<T>> {

    private final Function<T, V> mapper;
    private final LanguageText errorMessage;

    public UniqueListBy(Function<T, V> mapper) {
        this(mapper, new LanguageText().en("List contains duplicate entries").de("Liste enthält doppelte Einträge"));
    }

    public UniqueListBy(Function<T, V> mapper, LanguageText errorMessage) {
        this.mapper = mapper;
        this.errorMessage = errorMessage;
    }


    @Override
    public ValidationResult validate(List<T> list) {
        boolean error = false;
        HashSet<V> set = new HashSet<>();
        for (T item : list) {
            if (!set.add(mapper.apply(item))) {
                error = true;
            }
        }
        return new ValidationResult(error, errorMessage);
    }
}
