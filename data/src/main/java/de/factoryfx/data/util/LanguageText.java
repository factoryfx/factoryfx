package de.factoryfx.data.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LanguageText {
    @JsonProperty
    private Map<Locale, String> texts = new HashMap<>();


    public LanguageText() {
    }


    public LanguageText en(String text) {
        put(Locale.ENGLISH,text);
        return this;
    }

    public LanguageText de(String text) {
        put(Locale.GERMAN,text);
        return this;
    }

    public LanguageText es(String text) {
        put(new Locale("es", "ES"),text);
        return this;
    }

    public LanguageText fr(String text) {
        put(Locale.FRANCE,text);
        return this;
    }

    public LanguageText it(String text) {
        put(Locale.ITALIAN,text);
        return this;
    }

    public LanguageText pt(String text) {
        put(new Locale("pt", "PT"),text);
        return this;
    }

    /** get the text in the locale if possible else return the text in a random(probably only english) locale or null if no text avaible*/
    public String getPreferred(Locale locale) {
        String text = texts.get(locale);
        if (text==null){
            for (Map.Entry<Locale,String> entry: texts.entrySet()){
                return entry.getValue();
            }
        }
        return text;
    }

    public LanguageText put(Locale locale, String text) {
        texts.put(locale, text);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Locale,String> entry: texts.entrySet()){
            result.append(entry.getKey());
            result.append(":");
            result.append(entry.getValue());
            result.append(", ");
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LanguageText that = (LanguageText) o;
        return Objects.equals(texts, that.texts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texts);
    }
}