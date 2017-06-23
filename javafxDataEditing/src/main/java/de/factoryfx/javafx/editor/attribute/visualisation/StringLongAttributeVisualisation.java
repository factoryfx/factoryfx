package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

public class StringLongAttributeVisualisation extends ValueAttributeEditorVisualisation<String> {


    @Override
    public Node createVisualisation(SimpleObjectProperty<String> attributeValue, boolean readonly) {
        TextArea textArea = new TextArea();
        textArea.textProperty().bindBidirectional(attributeValue);
        textArea.setEditable(!readonly);
        return textArea;
    }
}
