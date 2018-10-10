package de.factoryfx.javafx.data.editor.attribute;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeChangeListener;
import de.factoryfx.data.attribute.WeakAttributeChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import java.util.List;


public abstract class ListAttributeVisualisation<T, A extends Attribute<List<T>,A>> extends ValueAttributeVisualisation<List<T>,A> {
    public final ObservableList<T> readOnlyObservableList = FXCollections.observableArrayList();
    public final AttributeChangeListener<List<T>, A> attributeChangeListener;

    protected ListAttributeVisualisation(A boundAttribute, ValidationDecoration validationDecoration) {
        super(boundAttribute, validationDecoration);

        attributeChangeListener = (attribute, value) -> readOnlyObservableList.setAll(observableAttributeValue.get());
        boundAttribute.internal_addListener(new WeakAttributeChangeListener<>(attributeChangeListener));
        readOnlyObservableList.setAll(observableAttributeValue.get());
    }

    @Override
    public Node createValueVisualisation() {
        return createValueListVisualisation();
    }

    public abstract Node createValueListVisualisation();
}
