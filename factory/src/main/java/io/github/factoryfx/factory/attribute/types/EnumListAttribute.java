package io.github.factoryfx.factory.attribute.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.factoryfx.factory.attribute.ValueListAttribute;
import io.github.factoryfx.factory.util.LanguageText;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @param <E> enum class
 */
public class EnumListAttribute<E extends Enum<E>> extends ValueListAttribute<E,EnumListAttribute<E>> {

    public EnumListAttribute() {
        super();
    }

    /**
     * workaround for: diamond operator doesn't work chained expression inference (Section D of JSR 335)
     * @param setup setup function
     */
    public EnumListAttribute(Consumer<EnumListAttribute<E>> setup){
        super();
        setup.accept(this);
    }

    @JsonIgnore
    private EnumTranslations<E> enumTranslations;

    public EnumListAttribute<E> deEnum(E value, String text){
        if (enumTranslations==null){
            enumTranslations = new EnumTranslations<>();
        }
        enumTranslations.deEnum(value,text);
        return this;
    }

    public EnumListAttribute<E> enEnum(E value, String text){
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
