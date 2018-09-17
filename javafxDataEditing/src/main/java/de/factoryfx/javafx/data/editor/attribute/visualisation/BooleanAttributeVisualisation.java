package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.javafx.data.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

public class BooleanAttributeVisualisation extends ValueAttributeEditorVisualisation<Boolean> {

    private final boolean userReadOnly;

    public BooleanAttributeVisualisation(boolean userReadOnly) {
        this.userReadOnly = userReadOnly;
    }


    @Override
    public Node createVisualisation(SimpleObjectProperty<Boolean> boundTo, boolean readonly) {
        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().bindBidirectional(boundTo);
        checkBox.setDisable(readonly || userReadOnly);
        return checkBox;
    }
}
