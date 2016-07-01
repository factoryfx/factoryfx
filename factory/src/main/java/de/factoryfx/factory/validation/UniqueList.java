package de.factoryfx.factory.validation;

import java.util.HashSet;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.util.LanguageText;
import javafx.collections.ObservableList;

public class UniqueList<T extends FactoryBase> implements Validation<ObservableList<T>> {

    @Override
    public LanguageText getValidationDescription() {
        return new LanguageText().en("List contains dublicates entries");
    }

    @Override
    public boolean validate(ObservableList<T> list) {
        HashSet<String> set = new HashSet<>();
        for (T item : list) {
            if (!set.add(item.getId())) {
                return false;
            }
        }
        return true;
    }
}
