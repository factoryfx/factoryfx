package io.github.factoryfx.javafx.editor.attribute.visualisation;

import java.net.URI;

import io.github.factoryfx.factory.attribute.types.URIAttribute;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.editor.attribute.ValueAttributeVisualisation;
import io.github.factoryfx.javafx.editor.attribute.converter.URIStringConverter;
import io.github.factoryfx.javafx.util.TypedTextFieldHelper;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class URIAttributeVisualisation extends ValueAttributeVisualisation<URI, URIAttribute> {

    public URIAttributeVisualisation(URIAttribute attribute, ValidationDecoration validationDecoration) {
        super(attribute,validationDecoration);
    }

    @Override
    public Node createValueVisualisation() {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupURITextField(textField);
        textField.textProperty().bindBidirectional(observableAttributeValue, new URIStringConverter());
        textField.disableProperty().bind(readOnly);
        return textField;
    }
}
