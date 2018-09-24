package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
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
