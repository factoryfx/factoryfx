package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.util.TypedTextFieldHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.converter.LongStringConverter;

public class LongAttributeVisualisation implements AttributeEditorVisualisation<Long> {

    @Override
    public Node createContent(SimpleObjectProperty<Long> boundTo) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupLongTextField(textField);
        textField.textProperty().bindBidirectional(boundTo, new LongStringConverter());
        return textField;
    }
}
