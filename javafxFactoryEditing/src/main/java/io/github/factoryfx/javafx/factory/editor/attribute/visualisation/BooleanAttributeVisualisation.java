package io.github.factoryfx.javafx.factory.editor.attribute.visualisation;

import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;
import io.github.factoryfx.javafx.factory.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.factory.editor.attribute.ValueAttributeVisualisation;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

public class BooleanAttributeVisualisation extends ValueAttributeVisualisation<Boolean, BooleanAttribute> {

    public BooleanAttributeVisualisation(BooleanAttribute booleanAttribute, ValidationDecoration validationDecoration) {
        super(booleanAttribute, validationDecoration);
    }


    @Override
    public Node createValueVisualisation() {
        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().bindBidirectional(observableAttributeValue);
        checkBox.disableProperty().bind(readOnly);
        return checkBox;
    }
}
