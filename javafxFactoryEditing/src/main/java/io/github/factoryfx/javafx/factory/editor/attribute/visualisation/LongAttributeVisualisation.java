package io.github.factoryfx.javafx.factory.editor.attribute.visualisation;

import io.github.factoryfx.factory.attribute.primitive.LongAttribute;
import io.github.factoryfx.javafx.factory.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.factory.editor.attribute.ValueAttributeVisualisation;
import io.github.factoryfx.javafx.factory.util.TypedTextFieldHelper;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.converter.LongStringConverter;

public class LongAttributeVisualisation extends ValueAttributeVisualisation<Long, LongAttribute> {

    public LongAttributeVisualisation(LongAttribute attribute, ValidationDecoration validationDecoration) {
        super(attribute, validationDecoration);
    }

    @Override
    public Node createValueVisualisation() {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupLongTextField(textField);
        textField.textProperty().bindBidirectional(observableAttributeValue, new LongStringConverter());
        textField.disableProperty().bind(readOnly);
        return textField;
    }
}
