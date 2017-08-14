package de.factoryfx.data.validation;

import java.util.HashSet;
import java.util.List;

import de.factoryfx.data.Data;
import de.factoryfx.data.util.LanguageText;

public class UniqueList<T extends Data> implements Validation<List<T>> {

    @Override
    public ValidationResult validate(List<T> list) {
        boolean error=false;
        HashSet<Object> set = new HashSet<>();
        for (T item : list) {
            if (!set.add(item.getId())) {
                error = true;
            }
        }
        return new ValidationResult(error,new LanguageText().en("List contains duplicates entries"));
    }
}
