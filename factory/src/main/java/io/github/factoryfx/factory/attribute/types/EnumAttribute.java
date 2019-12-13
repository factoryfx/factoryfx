package io.github.factoryfx.factory.attribute.types;

import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;
import io.github.factoryfx.factory.util.LanguageText;

/**
 * @param <E> enum class
 */
public class EnumAttribute<E extends Enum<E>> extends ImmutableValueAttribute<E,EnumAttribute<E>> {


    public EnumAttribute() {
        super();
    }

    /**
     * workaround for: diamond operator doesn't work chained expression inference (Section D of JSR 335)
     * @param setup setup function
     */
    public EnumAttribute(Consumer<EnumAttribute<E>> setup){
        super();
        setup.accept(this);
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

    public String internal_enumDisplayText(E enumValue,Function<LanguageText,String> uniformDesign){
        if (enumValue==null){
            return "-";
        }
        if (enumTranslations!=null){
            return enumTranslations.getDisplayText(enumValue,uniformDesign);
        }
        return enumValue.name();
    }




}
