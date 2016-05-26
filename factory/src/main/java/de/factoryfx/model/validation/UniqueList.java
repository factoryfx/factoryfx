package de.factoryfx.model.validation;

import java.util.HashSet;

import de.factoryfx.model.FactoryBase;
import javafx.collections.ObservableList;

public class UniqueList<T extends FactoryBase> extends SimpleValidation<ObservableList<T>> {
    public UniqueList() {
        super(list -> {
            HashSet<String> set = new HashSet<>();
            for (T item : list) {
                if (!set.add(item.getId())) {
                    return new ValidationResult(false, "List contains dublicates entries");
                }
            }
            return new ValidationResult(true, "List contains dublicates entries");
        }, "List contains dublicates entries");
    }
}
