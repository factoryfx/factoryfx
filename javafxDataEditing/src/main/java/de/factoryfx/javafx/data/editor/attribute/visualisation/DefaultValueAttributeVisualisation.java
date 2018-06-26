package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.javafx.data.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class DefaultValueAttributeVisualisation extends ValueAttributeEditorVisualisation<Object> {
    @Override
    public Node createVisualisation(SimpleObjectProperty<Object> boundTo, boolean readonly) {
        Label label = new Label("value is not editable");
        return label;
    }
}
