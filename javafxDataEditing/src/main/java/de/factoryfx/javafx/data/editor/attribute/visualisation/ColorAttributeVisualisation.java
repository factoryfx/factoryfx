package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.javafx.data.attribute.ColorAttribute;
import de.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
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
