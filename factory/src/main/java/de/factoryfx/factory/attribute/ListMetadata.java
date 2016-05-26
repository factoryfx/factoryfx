package de.factoryfx.factory.attribute;

import de.factoryfx.factory.validation.Validation;
import javafx.collections.ObservableList;

//shortcut for less verbose AttributeMetadata<List<T>>
public class ListMetadata<T> extends AttributeMetadata<ObservableList<T>> {

    @SafeVarargs
    public ListMetadata(String displayName, Validation<ObservableList<T>>... validation) {
        super(displayName, validation);
    }

    public ListMetadata(String displayName) {
        super(displayName);
    }
}
