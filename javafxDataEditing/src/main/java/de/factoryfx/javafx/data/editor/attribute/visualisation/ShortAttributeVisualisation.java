package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.javafx.data.editor.attribute.ValueAttributeEditorVisualisation;
import de.factoryfx.javafx.data.util.TypedTextFieldHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.converter.ShortStringConverter;

public class ShortAttributeVisualisation extends ValueAttributeEditorVisualisation<Short> {

    @Override
    public Node createVisualisation(SimpleObjectProperty<Short> boundTo, boolean readonly) {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupShortTextField(textField);
        textField.textProperty().bindBidirectional(boundTo, new ShortStringConverter());
        textField.setEditable(!readonly);
        return textField;
    }
}
