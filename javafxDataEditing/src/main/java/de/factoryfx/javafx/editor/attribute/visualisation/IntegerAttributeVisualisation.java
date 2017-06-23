package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import de.factoryfx.javafx.util.TypedTextFieldHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.converter.IntegerStringConverter;

public class IntegerAttributeVisualisation extends ValueAttributeEditorVisualisation<Integer> {

    @Override
    public Node createVisualisation(SimpleObjectProperty<Integer> boundTo, boolean readonly) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupIntegerTextField(textField);
        textField.textProperty().bindBidirectional(boundTo, new IntegerStringConverter());
        textField.setEditable(!readonly);
        return textField;
    }
}
