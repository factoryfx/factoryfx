package de.factoryfx.data.attribute.time;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

import java.time.LocalDate;

public class LocalDateAttribute extends ImmutableValueAttribute<LocalDate,LocalDateAttribute> {

    public LocalDateAttribute() {
        super(LocalDate.class);
    }

    @JsonCreator
    LocalDateAttribute(LocalDate initialValue) {
        super(LocalDate.class);
        set(initialValue);
    }

    @Override
    protected LocalDateAttribute createNewEmptyInstance() {
        return new LocalDateAttribute();
    }
}
