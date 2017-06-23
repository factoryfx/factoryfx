package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class DefaultValueAttributeVisualisation extends ValueAttributeEditorVisualisation<Object> {
    @Override
    public Node createVisualisation(SimpleObjectProperty<Object> boundTo, boolean readonly) {
        Label label = new Label();
        label.textProperty().bind(StringBinding.stringExpression(boundTo));
        return label;
    }
}
