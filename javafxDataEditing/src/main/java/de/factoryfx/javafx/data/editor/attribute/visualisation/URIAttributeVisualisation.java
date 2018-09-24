package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.net.URI;

import de.factoryfx.data.attribute.types.URIAttribute;
import de.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import de.factoryfx.javafx.data.editor.attribute.converter.URIStringConverter;
import de.factoryfx.javafx.data.util.TypedTextFieldHelper;
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
