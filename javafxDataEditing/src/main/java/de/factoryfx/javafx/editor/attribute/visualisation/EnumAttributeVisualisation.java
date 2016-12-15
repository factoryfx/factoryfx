package de.factoryfx.javafx.editor.attribute.visualisation;

import java.util.Collection;

import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

public class EnumAttributeVisualisation extends ValueAttributeEditorVisualisation<Enum> {
    private final Collection<Enum> possibleEnumConstants;

    public EnumAttributeVisualisation(Collection<Enum> possibleEnumConstants) {
        this.possibleEnumConstants = possibleEnumConstants;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node createContent(SimpleObjectProperty<Enum> boundTo) {
        ComboBox<Enum> comboBox = new ComboBox<>();
        comboBox.setEditable(false);
        comboBox.getItems().addAll(possibleEnumConstants);
        comboBox.valueProperty().bindBidirectional(boundTo);
        return comboBox;
    }
}
