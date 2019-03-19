package io.github.factoryfx.javafx.data.editor.attribute.visualisation;

import io.github.factoryfx.data.attribute.primitive.DoubleAttribute;
import io.github.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import io.github.factoryfx.javafx.data.util.TypedTextFieldHelper;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.converter.DoubleStringConverter;

public class DoubleAttributeVisualisation extends ValueAttributeVisualisation<Double, DoubleAttribute> {

    public DoubleAttributeVisualisation(DoubleAttribute attribute, ValidationDecoration validationDecoration) {
        super(attribute, validationDecoration);
    }

    @Override
    public Node createValueVisualisation() {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupDoubleTextField(textField);
        textField.textProperty().bindBidirectional(observableAttributeValue, new DoubleStringConverter());
        textField.disableProperty().bind(readOnly);
        return textField;
    }
}
