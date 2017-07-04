package de.factoryfx.javafx.editor.attribute;

import de.factoryfx.data.attribute.Attribute;
import javafx.scene.Node;

import java.util.List;

public abstract class ListAttributeEditorVisualisation<T> implements AttributeEditorVisualisation<List<T>> {
    protected List<T> attributeValue;
    @Override
    public void init(Attribute<List<T>,?> boundAttribute) {
        this.attributeValue=boundAttribute.get();
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

    public abstract Node createContent(List<T> attributeValue, boolean readonly);

}
