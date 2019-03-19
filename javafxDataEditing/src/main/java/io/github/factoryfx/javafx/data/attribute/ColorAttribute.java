package io.github.factoryfx.javafx.data.attribute;

import io.github.factoryfx.data.attribute.ImmutableValueAttribute;
import javafx.scene.paint.Color;

public class ColorAttribute extends ImmutableValueAttribute<Color,ColorAttribute> {

    public ColorAttribute() {
        super(Color.class);
    }

}
