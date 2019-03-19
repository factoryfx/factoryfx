package io.github.factoryfx.data.attribute.types;

import java.util.*;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.factoryfx.data.attribute.ImmutableValueAttribute;
import io.github.factoryfx.data.util.LanguageText;

/**
 * @param <E> enum class
 */
public class EnumAttribute<E extends Enum<E>> extends ImmutableValueAttribute<E,EnumAttribute<E>> {

    private final Class<E> clazz;

    public EnumAttribute(Class<E> clazz) {
        super(null);  //null is fine cause internal_getAttributeType override
        this.clazz=clazz;
    }

    public List<E> internal_possibleEnumValues() {
        return new ArrayList<>(Arrays.asList(clazz.getEnumConstants()));
    }

    public Class<E> internal_getEnumClass() {
        return clazz;
    }



    @JsonIgnore
    private EnumTranslations<E> enumTranslations;

    public EnumAttribute<E> deEnum(E value, String text){
        if (enumTranslations==null){
            enumTranslations = new EnumTranslations<>();
        }
        enumTranslations.deEnum(value,text);
        return this;
    }

    public EnumAttribute<E> enEnum(E value, String text){
        if (enumTranslations==null){
            enumTranslations = new EnumTranslations<>();
        }
        enumTranslations.enEnum(value,text);
        return this;
    }

    public String internal_enumDisplayText(Enum<?> enumValue,Function<LanguageText,String> uniformDesign){
        if (enumValue==null){
            return "-";
        }
        if (enumTranslations!=null){
            return enumTranslations.getDisplayText(enumValue,uniformDesign);
        }
        return enumValue.name();
    }




}
