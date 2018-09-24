package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.data.attribute.primitive.ShortAttribute;
import de.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import de.factoryfx.javafx.data.util.TypedTextFieldHelper;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.converter.ShortStringConverter;

public class ShortAttributeVisualisation extends ValueAttributeVisualisation<Short, ShortAttribute> {

    public ShortAttributeVisualisation(ShortAttribute shortAttribute, ValidationDecoration validationDecoration) {
        super(shortAttribute,validationDecoration);
    }

    @Override
    public Node createValueVisualisation() {
        TextField textField = new TextField();
        TypedTextFieldHelper.setupShortTextField(textField);
        textField.textProperty().bindBidirectional(observableAttributeValue, new ShortStringConverter());
        textField.disableProperty().bind(readOnly);
        return textField;
    }
}
