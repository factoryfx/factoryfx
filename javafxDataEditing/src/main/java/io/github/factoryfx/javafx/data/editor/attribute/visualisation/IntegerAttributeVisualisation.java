package io.github.factoryfx.javafx.data.editor.attribute.visualisation;

import io.github.factoryfx.data.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import io.github.factoryfx.javafx.data.util.TypedTextFieldHelper;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.converter.IntegerStringConverter;

public class IntegerAttributeVisualisation extends ValueAttributeVisualisation<Integer, IntegerAttribute> {

    public IntegerAttributeVisualisation(IntegerAttribute attribute, ValidationDecoration validationDecoration) {
        super(attribute,validationDecoration);
    }

    @Override
    public Node createValueVisualisation() {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupIntegerTextField(textField);
        textField.textProperty().bindBidirectional(observableAttributeValue, new IntegerStringConverter());
        textField.disableProperty().bind(readOnly);
        return textField;
    }
}
