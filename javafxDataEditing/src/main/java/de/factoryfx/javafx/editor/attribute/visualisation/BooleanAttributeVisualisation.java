package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.ImmutableAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

public class BooleanAttributeVisualisation extends ImmutableAttributeEditorVisualisation<Boolean> {

    @Override
    public Node createContent(SimpleObjectProperty<Boolean> boundTo) {
        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().bindBidirectional(boundTo);
        return checkBox;
    }
}
