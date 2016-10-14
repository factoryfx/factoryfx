package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;
import javafx.scene.paint.Color;

public class ColorAttribute extends ValueAttribute<Color> {

    public ColorAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Color.class);
    }

    @JsonCreator
    ColorAttribute(Color initialValue) {
        super(null,Color.class);
        set(initialValue);
    }

}
