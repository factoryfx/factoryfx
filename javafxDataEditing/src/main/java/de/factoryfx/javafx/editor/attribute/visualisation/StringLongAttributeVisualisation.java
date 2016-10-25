package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

public class StringLongAttributeVisualisation implements AttributeEditorVisualisation<String> {

    @Override
    public Node createContent(SimpleObjectProperty<String> boundTo) {
        TextArea textField = new TextArea();
        textField.textProperty().bindBidirectional(boundTo);
        return textField;
    }
}
