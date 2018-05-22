package de.factoryfx.data.attribute.types;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class LocaleAttribute  extends ImmutableValueAttribute<Locale,LocaleAttribute> {

    public LocaleAttribute() {
        super(Locale.class);
    }

    @JsonCreator
    LocaleAttribute(Locale initialValue) {
        super(Locale.class);
        set(initialValue);
    }

}