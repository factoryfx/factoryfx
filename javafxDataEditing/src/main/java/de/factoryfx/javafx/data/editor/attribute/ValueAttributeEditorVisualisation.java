package de.factoryfx.javafx.data.editor.attribute;

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
    public Node createVisualisation() {
        return createVisualisation(attributeValue,false);
    }

    @Override
    public Node createReadOnlyVisualisation() {
        return createVisualisation(attributeValue,true);
    }

    public abstract Node createVisualisation(SimpleObjectProperty<T> attributeValue, boolean readonly);
}
