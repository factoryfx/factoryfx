package io.github.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.factoryfx.data.util.LanguageText;

import java.util.HashMap;
import java.util.function.Function;

public class EnumTranslations<E extends Enum<E>> {
    @JsonIgnore
    public HashMap<E,LanguageText> enumTranslations;

    public void deEnum(E value, String text){
        if (enumTranslations==null){
            enumTranslations = new HashMap<>();
        }
        LanguageText languageText = enumTranslations.computeIfAbsent(value, k -> new LanguageText());
        languageText.de(text);
    }

    public void enEnum(E value, String text){
        if (enumTranslations==null){
            enumTranslations = new HashMap<>();
        }
        LanguageText languageText = enumTranslations.computeIfAbsent(value, k -> new LanguageText());
        languageText.en(text);
    }

    public String getDisplayText(Enum<?> enumValue,Function<LanguageText,String> uniformDesign){
        if (enumValue==null){
            return "-";
        }
        if (enumTranslations!=null){
            LanguageText languageText = enumTranslations.get(enumValue);
            if (languageText!=null) {
                return uniformDesign.apply(languageText);
            }
        }
        return enumValue.name();

    }
}
