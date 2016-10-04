package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.util.TypedTextFieldHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.converter.DoubleStringConverter;

public class DoubleAttributeVisualisation implements AttributeEditorVisualisation<Double> {

    @Override
    public Node createContent(SimpleObjectProperty<Double> boundTo) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupDoubleTextField(textField);
        textField.textProperty().bindBidirectional(boundTo, new DoubleStringConverter());
        return textField;
    }
}
