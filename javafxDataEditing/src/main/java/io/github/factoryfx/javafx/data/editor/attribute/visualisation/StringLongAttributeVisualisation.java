package io.github.factoryfx.javafx.data.editor.attribute.visualisation;

import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

public class StringLongAttributeVisualisation extends ValueAttributeVisualisation<String, StringAttribute> {

    public StringLongAttributeVisualisation(StringAttribute attribute, ValidationDecoration validationDecoration) {
        super(attribute,validationDecoration);
    }

    @Override
    public Node createValueVisualisation() {
        TextArea textArea = new TextArea();
        textArea.textProperty().bindBidirectional(observableAttributeValue);
        textArea.disableProperty().bind(readOnly);
        return textArea;
    }
}
