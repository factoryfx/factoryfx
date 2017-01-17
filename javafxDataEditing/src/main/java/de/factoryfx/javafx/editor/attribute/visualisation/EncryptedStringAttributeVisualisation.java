package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class EncryptedStringAttributeVisualisation extends ValueAttributeEditorVisualisation<String> {

    @Override
    public Node createContent(SimpleObjectProperty<String> boundTo) {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(boundTo);
        return textField;
    }
}
