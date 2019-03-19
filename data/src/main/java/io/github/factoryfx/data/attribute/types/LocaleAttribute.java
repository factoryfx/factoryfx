package io.github.factoryfx.data.attribute.types;

import java.util.Locale;

import io.github.factoryfx.data.attribute.ImmutableValueAttribute;

public class LocaleAttribute  extends ImmutableValueAttribute<Locale,LocaleAttribute> {

    public LocaleAttribute() {
        super(Locale.class);
    }

}