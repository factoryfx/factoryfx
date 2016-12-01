package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.ImmutableAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

public class StringLongAttributeVisualisation extends ImmutableAttributeEditorVisualisation<String> {


    @Override
    public Node createContent(SimpleObjectProperty<String> attributeValue) {
        TextArea textField = new TextArea();
        textField.textProperty().bindBidirectional(attributeValue);
        return textField;
    }
}
