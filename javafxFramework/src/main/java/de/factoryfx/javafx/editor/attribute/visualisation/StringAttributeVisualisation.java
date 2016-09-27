package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class StringAttributeVisualisation implements AttributeEditorVisualisation<String> {

    @Override
    public Node createContent(SimpleObjectProperty<String> boundTo, Attribute<String> attribute) {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(boundTo);
        return textField;
    }
}
