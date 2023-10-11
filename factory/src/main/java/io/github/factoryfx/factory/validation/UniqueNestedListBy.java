package io.github.factoryfx.factory.validation;

import io.github.factoryfx.factory.util.LanguageText;

import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

public class UniqueNestedListBy<T, V, N> implements Validation<List<T>> {

    private final Function<T, List<V>> nestedListMapper;
    private final Function<V, N> mapper;
    private final LanguageText errorMessage;

    public UniqueNestedListBy(Function<T, List<V>> nestedListMapper, Function<V, N> mapper) {
        this(nestedListMapper, mapper, new LanguageText().en("Nested list contains duplicate entries").de("Geschachtelte Liste enthält doppelte Einträge"));
    }

    public UniqueNestedListBy(Function<T, List<V>> nestedListMapper, Function<V, N> mapper, LanguageText errorMessage) {
        this.nestedListMapper = nestedListMapper;
        this.mapper = mapper;
        this.errorMessage = errorMessage;
    }


    @Override
    public ValidationResult validate(List<T> list) {
        boolean error = false;
        HashSet<N> set = new HashSet<>();
        for (T item : list) {
            for (V el : nestedListMapper.apply(item)) {
                if (!set.add(mapper.apply(el))) {
                    error = true;
                }

            }
        }
        return new ValidationResult(error, errorMessage);
    }
}
