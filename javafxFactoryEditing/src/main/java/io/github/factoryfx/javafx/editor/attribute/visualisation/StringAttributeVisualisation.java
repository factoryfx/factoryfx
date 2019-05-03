package io.github.factoryfx.javafx.editor.attribute.visualisation;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.editor.attribute.ValueAttributeVisualisation;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class StringAttributeVisualisation extends ValueAttributeVisualisation<String, StringAttribute> {

    public StringAttributeVisualisation(StringAttribute attribute, ValidationDecoration validationDecoration) {
        super(attribute,validationDecoration);
    }

    @Override
    public Node createValueVisualisation() {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(observableAttributeValue);
        textField.disableProperty().bind(readOnly);
        return textField;
    }
}
