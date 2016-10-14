package de.factoryfx.data.attribute.types;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

public class LocaleAttribute  extends ValueAttribute<Locale> {

    public LocaleAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Locale.class);
    }

    @JsonCreator
    LocaleAttribute(Locale initialValue) {
        super(null,Locale.class);
        set(initialValue);
    }

}