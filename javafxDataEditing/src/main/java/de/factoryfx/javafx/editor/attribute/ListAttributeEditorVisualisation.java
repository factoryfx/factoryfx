package de.factoryfx.javafx.editor.attribute;

import de.factoryfx.data.attribute.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import java.util.Collection;
import java.util.List;

public abstract class ListAttributeEditorVisualisation<T> implements AttributeEditorVisualisation<List<T>> {
    private ObservableList<T> attributeValue= FXCollections.observableArrayList();
    private AttributeChangeListener attributeChangeListener;

    @Override
    @SuppressWarnings("unchecked")
    public void init(Attribute<List<T>,?> boundAttribute) {
        if (boundAttribute instanceof ReferenceListAttribute){
            attributeChangeListener = (attribute, value) -> attributeValue.setAll((Collection<T>) value);
            ((ReferenceListAttribute)boundAttribute).internal_addListener(new WeakAttributeChangeListener(attributeChangeListener));
            this.attributeValue.setAll(boundAttribute.get());
        }
        if (boundAttribute instanceof ValueListAttribute){
            attributeChangeListener = (attribute, value) -> attributeValue.setAll((Collection<T>) value);
            ((ValueListAttribute)boundAttribute).internal_addListener(new WeakAttributeChangeListener(attributeChangeListener));
            this.attributeValue.setAll(boundAttribute.get());
        }
    }

    @Override
    public void attributeValueChanged(List<T> newValue) {
        //nothing
    }

    @Override
    public Node createVisualisation() {
        return createContent(attributeValue,false);
    }

    @Override
    public Node createReadOnlyVisualisation() {
        return createContent(attributeValue,true);
    }

    public abstract Node createContent(ObservableList<T> attributeValue, boolean readonly);

}
