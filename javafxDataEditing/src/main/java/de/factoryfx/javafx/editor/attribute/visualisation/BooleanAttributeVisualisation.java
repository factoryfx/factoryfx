package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

public class BooleanAttributeVisualisation extends ValueAttributeEditorVisualisation<Boolean> {

    @Override
    public Node createVisualisation(SimpleObjectProperty<Boolean> boundTo, boolean readonly) {
        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().bindBidirectional(boundTo);
        checkBox.setDisable(readonly);
        return checkBox;
    }
}
