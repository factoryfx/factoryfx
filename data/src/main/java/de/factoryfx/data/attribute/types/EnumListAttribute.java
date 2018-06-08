package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.attribute.AttributeTypeInfo;
import de.factoryfx.data.attribute.ValueListAttribute;
import de.factoryfx.data.util.LanguageText;

import java.util.*;
import java.util.function.Function;

/**
 * @param <E> enum class
 */
public class EnumListAttribute<E extends Enum<E>> extends ValueListAttribute<E,EnumListAttribute<E>> {

    private Class<E> clazz;

    @SuppressWarnings("unchecked")
    public EnumListAttribute(Class<E> clazz) {
        super(clazz);//workaround for java generic bug
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

    public EnumListAttribute<E> deEnum(E value, String text){
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

    public EnumListAttribute<E> enEnum(E value, String text){
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
