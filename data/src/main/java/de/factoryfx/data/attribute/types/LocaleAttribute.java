package de.factoryfx.data.attribute.types;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class LocaleAttribute  extends ImmutableValueAttribute<Locale> {

    public LocaleAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Locale.class);
    }

    @JsonCreator
    LocaleAttribute(Locale initialValue) {
        super(null,Locale.class);
        set(initialValue);
    }

    @Override
    protected Attribute<Locale> createNewEmptyInstance() {
        return new LocaleAttribute(metadata);
    }
}