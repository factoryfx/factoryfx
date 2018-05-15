package de.factoryfx.data.validation;

import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

import de.factoryfx.data.util.LanguageText;

public class UniqueListBy<T, V> implements Validation<List<T>> {

    private final Function<T, V> mapper;

    public UniqueListBy(Function<T,V> mapper){
        this.mapper = mapper;
    }

    @Override
    public ValidationResult validate(List<T> list) {
        boolean error=false;
        HashSet<V> set = new HashSet<>();
        for (T item : list) {
            if (!set.add(mapper.apply(item))) {
                error = true;
            }
        }
        return new ValidationResult(error,new LanguageText().en("List contains duplicate entries").de("Liste enthält doppelte Einträge"));
    }
}
