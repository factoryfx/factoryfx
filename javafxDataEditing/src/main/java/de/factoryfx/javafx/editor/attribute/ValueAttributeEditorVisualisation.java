package de.factoryfx.javafx.editor.attribute;

import de.factoryfx.data.attribute.Attribute;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;


public abstract class ValueAttributeEditorVisualisation<T> implements AttributeEditorVisualisation<T> {

    private SimpleObjectProperty<T> attributeValue = new SimpleObjectProperty<>();


    boolean setLoop=false;
    @Override
    public void init(Attribute<T,?> boundAttribute) {
        this.attributeValue.set(boundAttribute.get());

        attributeValue.addListener((observable, oldValue, newValue1) -> {
            if (!setLoop){
                boundAttribute.set(newValue1);
            }
        });
    }

    @Override
    public void attributeValueChanged(T newValue) {
        setLoop = true;
        attributeValue.set(newValue);
        setLoop = false;
    }

    @Override
    public Node createContent() {
        return createContent(attributeValue);
    }


    public abstract Node createContent(SimpleObjectProperty<T> attributeValue);
}
