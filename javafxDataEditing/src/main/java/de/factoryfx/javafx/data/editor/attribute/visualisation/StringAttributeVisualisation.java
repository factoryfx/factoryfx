package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.javafx.data.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class StringAttributeVisualisation extends ValueAttributeEditorVisualisation<String> {

    @Override
    public Node createVisualisation(SimpleObjectProperty<String> boundTo, boolean readonly) {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(boundTo);
        textField.setEditable(!readonly);
        return textField;
    }
}
