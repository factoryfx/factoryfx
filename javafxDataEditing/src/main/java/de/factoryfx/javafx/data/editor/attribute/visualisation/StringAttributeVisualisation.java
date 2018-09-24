package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
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
