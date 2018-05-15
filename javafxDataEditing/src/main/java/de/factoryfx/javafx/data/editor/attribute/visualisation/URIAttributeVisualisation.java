package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.net.URI;

import de.factoryfx.javafx.data.editor.attribute.ValueAttributeEditorVisualisation;
import de.factoryfx.javafx.data.editor.attribute.converter.URIStringConverter;
import de.factoryfx.javafx.data.util.TypedTextFieldHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class URIAttributeVisualisation extends ValueAttributeEditorVisualisation<URI> {

    @Override
    public Node createVisualisation(SimpleObjectProperty<URI> boundTo, boolean readonly) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupURITextField(textField);
        textField.textProperty().bindBidirectional(boundTo, new URIStringConverter());
        textField.setEditable(!readonly);
        return textField;
    }
}
