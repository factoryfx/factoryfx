package de.factoryfx.javafx.data.editor.attribute;

import de.factoryfx.data.attribute.Attribute;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import java.util.List;


public abstract class ListAttributeVisualisation<T, A extends Attribute<List<T>,A>> extends ValueAttributeVisualisation<List<T>,A> {
    public ObservableList<T> readOnlyObservableList = FXCollections.observableArrayList();

    protected ListAttributeVisualisation(A boundAttribute, ValidationDecoration validationDecoration) {
        super(boundAttribute, validationDecoration);
        observableAttributeValue.addListener(observable -> readOnlyObservableList.setAll(observableAttributeValue.get()));
    }

    @Override
    public Node createValueVisualisation() {
        return createValueListVisualisation();
    }

    public abstract Node createValueListVisualisation();
}
