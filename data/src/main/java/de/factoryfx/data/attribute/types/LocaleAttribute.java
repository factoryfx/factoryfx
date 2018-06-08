package de.factoryfx.data.attribute.types;

import java.util.Locale;

import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class LocaleAttribute  extends ImmutableValueAttribute<Locale,LocaleAttribute> {

    public LocaleAttribute() {
        super(Locale.class);
    }

}