package de.factoryfx.javafx.editor.attribute.visualisation;

import java.util.Arrays;
import java.util.Collection;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

public class EnumAttributeVisualisation implements AttributeEditorVisualisation<Enum> {

    @Override
    @SuppressWarnings("unchecked")
    public Node createContent(SimpleObjectProperty<Enum> boundTo, Attribute<Enum> attribute) {
        ComboBox<Enum> comboBox = new ComboBox<>();
        comboBox.setEditable(false);
        comboBox.getItems().addAll((Collection<? extends Enum>) Arrays.asList(attribute.getAttributeType().dataType.getEnumConstants()));
        comboBox.valueProperty().bindBidirectional(boundTo);
        return comboBox;
    }
}
