package de.factoryfx.javafx.editor.attribute;

import de.factoryfx.data.attribute.Attribute;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public abstract class ListAttributeEditorVisualisation<T> implements AttributeEditorVisualisation<ObservableList<T>> {
    protected ObservableList<T> attributeValue;
    @Override
    public void init(Attribute<ObservableList<T>> boundAttribute) {
        this.attributeValue=boundAttribute.get();
    }

    @Override
    public void attributeValueChanged(ObservableList<T> newValue) {
        //nothing
    }

    @Override
    public Node createContent() {
        return createContent(attributeValue);
    }


    public abstract Node createContent(ObservableList<T> attributeValue);

}
