package de.factoryfx.factory.attribute.util;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ValueAttribute;
import de.factoryfx.factory.util.LanguageText;

public class I18nAttribute extends ValueAttribute<LanguageText,I18nAttribute> {
    public I18nAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata, LanguageText.class);
    }

    @JsonCreator
    I18nAttribute(LanguageText value) {
        super(null,null);
        set(value);
    }

    public I18nAttribute en(String text) {
        if (get()==null){
            set(new LanguageText());
        }
        get().en(text);
        return this;
    }

    public I18nAttribute de(String text) {
        if (get()==null){
            set(new LanguageText());
        }
        get().de(text);
        return this;
    }

    public String getPreferred(Locale locale) {
        return get().getPreferred(locale);
    }
}
