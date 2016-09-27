package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.util.TypedTextFieldHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.converter.IntegerStringConverter;

public class IntegerAttributeVisualisation implements AttributeEditorVisualisation<Integer> {

    @Override
    public Node createContent(SimpleObjectProperty<Integer> boundTo, Attribute<Integer> attribute) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupIntegerTextField(textField);
        textField.textProperty().bindBidirectional(boundTo, new IntegerStringConverter());
        return textField;
    }
}
