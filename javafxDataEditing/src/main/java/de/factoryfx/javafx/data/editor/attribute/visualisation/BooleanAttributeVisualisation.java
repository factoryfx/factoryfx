package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
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
