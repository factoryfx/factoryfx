package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.editor.attribute.converter.URIStringConverter;
import de.factoryfx.javafx.util.TypedTextFieldHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.net.URI;

public class URIAttributeVisualisation implements AttributeEditorVisualisation<URI> {

    @Override
    public Node createContent(SimpleObjectProperty<URI> boundTo) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupURITextField(textField);
        textField.textProperty().bindBidirectional(boundTo, new URIStringConverter());
        return textField;
    }
}
