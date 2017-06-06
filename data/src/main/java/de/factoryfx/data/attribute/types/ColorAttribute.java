package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;
import javafx.scene.paint.Color;

public class ColorAttribute extends ImmutableValueAttribute<Color> {

    public ColorAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Color.class);
    }

    @JsonCreator
    ColorAttribute(Color initialValue) {
        super(null,Color.class);
        set(initialValue);
    }

    @Override
    protected Attribute<Color> createNewEmptyInstance() {
        return new ColorAttribute(metadata);
    }
}
