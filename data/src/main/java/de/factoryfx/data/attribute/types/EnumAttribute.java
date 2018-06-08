package de.factoryfx.data.attribute.types;

import java.util.*;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.factoryfx.data.attribute.AttributeTypeInfo;
import de.factoryfx.data.attribute.ImmutableValueAttribute;
import de.factoryfx.data.util.LanguageText;

/**
 * @param <E> enum class
 */
public class EnumAttribute<E extends Enum<E>> extends ImmutableValueAttribute<E,EnumAttribute<E>> {

    private Class<E> clazz;

    @Override
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    protected E getValue() {
        return super.getValue();
    }



    @SuppressWarnings("unchecked")
    public EnumAttribute(Class<E> clazz) {
        super(null);  //null is fine cause internal_getAttributeType override
        this.clazz=clazz;
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {

        return new AttributeTypeInfo(clazz,null,null,AttributeTypeInfo.AttributeTypeCategory.VALUE);
    }

    public List<Enum<E>> internal_possibleEnumValues() {
        return new ArrayList<>(Arrays.asList(clazz.getEnumConstants()));
    }


    @JsonIgnore
    public HashMap<E,LanguageText> enumTranslations;

    public EnumAttribute<E> deEnum(E value, String text){
        if (enumTranslations==null){
            enumTranslations = new HashMap<>();
        }
        LanguageText languageText = enumTranslations.get(value);
        if (languageText == null) {
            languageText = new LanguageText();
            enumTranslations.put(value,languageText);
        }
        languageText.de(text);
        return this;
    }

    public EnumAttribute<E> enEnum(E value, String text){
        if (enumTranslations==null){
            enumTranslations = new HashMap<>();
        }
        LanguageText languageText = enumTranslations.get(value);
        if (languageText == null) {
            languageText = new LanguageText();
            enumTranslations.put(value,languageText);
        }
        languageText.en(text);
        return this;
    }

    public String internal_enumDisplayText(E enumValue,Function<LanguageText,String> uniformDesign){
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

    @SuppressWarnings("unchecked")
    public String internal_enumDisplayText(Object enumValue,Function<LanguageText,String> uniformDesign){
        return internal_enumDisplayText((E)enumValue,uniformDesign);
    }


}
