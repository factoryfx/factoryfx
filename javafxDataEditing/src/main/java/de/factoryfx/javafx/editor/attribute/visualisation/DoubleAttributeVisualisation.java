package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import de.factoryfx.javafx.util.TypedTextFieldHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.converter.DoubleStringConverter;

public class DoubleAttributeVisualisation extends ValueAttributeEditorVisualisation<Double> {

    @Override
    public Node createContent(SimpleObjectProperty<Double> boundTo) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupDoubleTextField(textField);
        textField.textProperty().bindBidirectional(boundTo, new DoubleStringConverter());
        return textField;
    }
}
