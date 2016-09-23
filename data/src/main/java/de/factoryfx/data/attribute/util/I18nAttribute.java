package de.factoryfx.data.attribute.util;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;
import de.factoryfx.data.util.LanguageText;

public class I18nAttribute extends ValueAttribute<LanguageText> {
    public I18nAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata, LanguageText.class);
    }

    @JsonCreator
    I18nAttribute(LanguageText value) {
        super(null,null);
        set(value);
    }

    public I18nAttribute en(String text) {
        getOrCreate().en(text);
        return this;
    }

    public I18nAttribute de(String text) {
        getOrCreate().de(text);
        return this;
    }

    public I18nAttribute es(String text) {
        getOrCreate().es(text);
        return this;
    }

    public I18nAttribute fr(String text) {
        getOrCreate().fr(text);
        return this;
    }

    public I18nAttribute it(String text) {
        getOrCreate().it(text);
        return this;
    }

    public I18nAttribute pt(String text) {
        getOrCreate().pt(text);
        return this;
    }

    private LanguageText getOrCreate(){
        if (get()==null){
            set(new LanguageText());
        }
        return get();
    }


    public String getPreferred(Locale locale) {
        return get().getPreferred(locale);
    }
}
