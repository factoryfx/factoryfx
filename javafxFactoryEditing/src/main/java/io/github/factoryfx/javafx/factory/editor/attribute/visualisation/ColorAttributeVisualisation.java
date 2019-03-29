package io.github.factoryfx.javafx.factory.editor.attribute.visualisation;

import io.github.factoryfx.javafx.factory.editor.attribute.ColorAttribute;
import io.github.factoryfx.javafx.factory.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.factory.editor.attribute.ValueAttributeVisualisation;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class ColorAttributeVisualisation extends ValueAttributeVisualisation<Color, ColorAttribute> {

    public ColorAttributeVisualisation(ColorAttribute attribute, ValidationDecoration validationDecoration) {
        super(attribute, validationDecoration);
    }

    @Override
    public Node createValueVisualisation() {
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.valueProperty().bindBidirectional(observableAttributeValue);
        return colorPicker;
    }
}
