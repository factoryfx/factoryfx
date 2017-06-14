package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class ObjectValueAttributeVisualisation extends ValueAttributeEditorVisualisation<Object> {
    @Override
    public Node createContent(SimpleObjectProperty<Object> boundTo) {
        Label label = new Label();
        label.textProperty().bind(StringBinding.stringExpression(boundTo));
        return label;
    }
}
