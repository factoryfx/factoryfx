package de.factoryfx.javafx.data.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ImmutableValueAttribute;
import javafx.scene.paint.Color;

public class ColorAttribute extends ImmutableValueAttribute<Color,ColorAttribute> {

    public ColorAttribute() {
        super(Color.class);
    }

    @JsonCreator
    ColorAttribute(Color initialValue) {
        super(Color.class);
        set(initialValue);
    }

}
