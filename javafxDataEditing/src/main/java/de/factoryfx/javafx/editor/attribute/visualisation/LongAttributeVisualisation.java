package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import de.factoryfx.javafx.util.TypedTextFieldHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.converter.LongStringConverter;

public class LongAttributeVisualisation extends ValueAttributeEditorVisualisation<Long> {

    @Override
    public Node createVisualisation(SimpleObjectProperty<Long> boundTo, boolean readonly) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupLongTextField(textField);
        textField.textProperty().bindBidirectional(boundTo, new LongStringConverter());
        textField.setEditable(!readonly);
        return textField;
    }
}
