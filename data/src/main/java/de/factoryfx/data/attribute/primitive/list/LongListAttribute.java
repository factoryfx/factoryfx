package de.factoryfx.data.attribute.primitive.list;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ValueListAttribute;

public class LongListAttribute extends ValueListAttribute<Long,LongListAttribute> {
    @JsonCreator
    public LongListAttribute() {
        super(Long.class);
    }

}