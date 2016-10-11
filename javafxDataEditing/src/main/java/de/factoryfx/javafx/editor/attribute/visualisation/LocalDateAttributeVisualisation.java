package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.editor.attribute.converter.LocalDateStringConverter;
import de.factoryfx.javafx.util.TypedTextFieldHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class LocalDateAttributeVisualisation implements AttributeEditorVisualisation<LocalDate> {

    @Override
    public Node createContent(SimpleObjectProperty<LocalDate> boundTo) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupLocalDateTextField(textField);
        textField.textProperty().bindBidirectional(boundTo, new LocalDateStringConverter());
        return textField;
    }
}
