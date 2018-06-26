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

    private final Class<E> clazz;

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

    public List<E> internal_possibleEnumValues() {
        return new ArrayList<>(Arrays.asList(clazz.getEnumConstants()));
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
