package de.factoryfx.data.attribute.types;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.factoryfx.data.attribute.AttributeTypeInfo;
import de.factoryfx.data.attribute.ImmutableValueAttribute;
import de.factoryfx.data.util.LanguageText;

/**
 * @param <E> enum class
 */
public class EnumAttribute<E extends Enum<E>> extends ImmutableValueAttribute<EnumAttribute.EnumWrapper<E>,EnumAttribute<E>> {

    @JsonCreator
    EnumAttribute(EnumWrapper<E> value) {
        super(null);
        set(value);
    }
    private Class<E> clazz;

    @SuppressWarnings("unchecked")
    public EnumAttribute(Class<E> clazz) {
        super((Class<EnumWrapper<E>>) EnumWrapper.class.asSubclass(EnumWrapper.class));//workaround for java generic bug
        this.clazz=clazz;
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {

        return new AttributeTypeInfo(clazz,null,null,AttributeTypeInfo.AttributeTypeCategory.VALUE);
    }

    public List<Enum<E>> internal_possibleEnumValues() {
        return new ArrayList<>(Arrays.asList(clazz.getEnumConstants()));
    }

    public List<EnumAttribute.EnumWrapper<?>> internal_possibleEnumWrapperValues() {
        return Stream.of(clazz.getEnumConstants()).map(EnumWrapper::new).collect(Collectors.toList());
    }

    public E getEnum() {
        return Optional.ofNullable(get()).map(e->e.enumField).orElse(null);
    }

    /**
     * the default set value method
     * @param enumValue value
     */
    @SuppressWarnings("unchecked")
    public void setEnum(E enumValue) {
        set(new EnumWrapper<>(enumValue));
    }

    @SuppressWarnings("unchecked")
    public EnumAttribute<E> defaultEnum(E anEnum){
        set(new EnumWrapper<>(anEnum));
        return this;
    }

    /***
     * use {@link #setEnum} instead (workaround for enums json serialisation)
     * @param value wrapper for workaround
     */
    @Override
    public void set(EnumWrapper<E> value) {
        super.set(value);
    }

    public void set(E anEnum) {
        set(new EnumWrapper<>(anEnum));
    }

    /***
     * use {@link #getEnum} instead (workaround for enums json serialisation)
     * @return wrapper
     */
    @Override
    public EnumWrapper<E> get() {
        return super.get();
    }

    //Workaround for bug https://github.com/FasterXML/jackson-databind/issues/937
    //@JsonValue doesn't work with JsonTypeInfo
    public static class EnumWrapper<E extends Enum<E>>{
        @JsonProperty
        public final E enumField;
        @JsonProperty
        private final Class<E> enumClass;


//        public EnumWrapper(Enum<?> enumField) {
//            this.enumField = (T) enumField;
//            this.enumClass= (Class<T>) enumField.getClass();
//        }

        @SuppressWarnings("unchecked")
        public EnumWrapper(E enumField) {
            this.enumField = enumField;
            this.enumClass= (Class<E>) enumField.getClass();
        }

        //TODO remove, used only for quickfix compatibility
        public EnumWrapper(String garbage) {
            enumField=null;
            enumClass=null;
        }

        @JsonCreator
        protected EnumWrapper(@JsonProperty("enumField")String enumField, @JsonProperty("enumClass")Class<E> enumClass) {
            this.enumClass= enumClass;
            this.enumField = enumClass==null?null:Arrays.stream(this.enumClass.getEnumConstants()).filter(t -> t.name().equals(enumField)).findAny().orElseGet(null);
        }

        @Override
        public String toString(){
            return enumField == null ? "" : enumField.name();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EnumWrapper<?> that = (EnumWrapper<?>) o;

            if (enumField != null ? !enumField.equals(that.enumField) : that.enumField != null) return false;
            return enumClass != null ? enumClass.equals(that.enumClass) : that.enumClass == null;
        }

        @Override
        public int hashCode() {
            int result = enumField != null ? enumField.hashCode() : 0;
            result = 31 * result + (enumClass != null ? enumClass.hashCode() : 0);
            return result;
        }
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
