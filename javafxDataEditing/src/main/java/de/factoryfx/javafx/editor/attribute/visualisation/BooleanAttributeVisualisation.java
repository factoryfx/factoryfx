package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

public class BooleanAttributeVisualisation extends ValueAttributeEditorVisualisation<Boolean> {

    @Override
    public Node createContent(SimpleObjectProperty<Boolean> boundTo) {
        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().bindBidirectional(boundTo);
        return checkBox;
    }
}
