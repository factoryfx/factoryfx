package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.javafx.data.editor.attribute.ValueAttributeEditorVisualisation;
import de.factoryfx.javafx.data.util.TypedTextFieldHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.converter.DoubleStringConverter;

public class DoubleAttributeVisualisation extends ValueAttributeEditorVisualisation<Double> {

    @Override
    public Node createVisualisation(SimpleObjectProperty<Double> boundTo, boolean readonly) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupDoubleTextField(textField);
        textField.textProperty().bindBidirectional(boundTo, new DoubleStringConverter());
        textField.setEditable(!readonly);
        return textField;
    }
}
