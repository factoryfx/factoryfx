package de.factoryfx.data.validation;

import java.util.HashSet;

import de.factoryfx.data.Data;
import de.factoryfx.data.util.LanguageText;
import javafx.collections.ObservableList;

public class UniqueList<T extends Data> implements Validation<ObservableList<T>> {

    @Override
    public LanguageText getValidationDescription() {
        return new LanguageText().en("List contains dublicates entries");
    }

    @Override
    public boolean validate(ObservableList<T> list) {
        HashSet<Object> set = new HashSet<>();
        for (T item : list) {
            if (!set.add(item.getId())) {
                return false;
            }
        }
        return true;
    }
}
