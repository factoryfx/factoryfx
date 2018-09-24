package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.data.attribute.primitive.LongAttribute;
import de.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import de.factoryfx.javafx.data.util.TypedTextFieldHelper;
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
