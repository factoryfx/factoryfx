package io.github.factoryfx.javafx.factory.editor.attribute.visualisation;

import io.github.factoryfx.factory.attribute.primitive.ShortAttribute;
import io.github.factoryfx.javafx.factory.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.factory.editor.attribute.ValueAttributeVisualisation;
import io.github.factoryfx.javafx.factory.util.TypedTextFieldHelper;
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
