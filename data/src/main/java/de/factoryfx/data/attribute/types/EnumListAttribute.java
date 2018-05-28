package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.attribute.AttributeTypeInfo;
import de.factoryfx.data.attribute.ValueListAttribute;
import de.factoryfx.data.util.LanguageText;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @param <E> enum class
 */
public class EnumListAttribute<E extends Enum<E>> extends ValueListAttribute<EnumListAttribute.EnumWrapper<E>,EnumListAttribute<E>> {

    @JsonCreator
    EnumListAttribute(List<EnumWrapper<E>> value) {
        super(null);
        set(value);
    }
    private Class<E> clazz;

    @SuppressWarnings("unchecked")
    public EnumListAttribute(Class<E> clazz) {
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

    public List<EnumListAttribute.EnumWrapper<?>> internal_possibleEnumWrapperValues() {
        return Stream.of(clazz.getEnumConstants()).map(EnumWrapper::new).collect(Collectors.toList());
    }

    public List<E> getEnumList() {
        List<EnumWrapper<E>> enumWrappers = super.get();
        ArrayList<E> ret = new ArrayList<>(enumWrappers.size());
        for (EnumWrapper<E> en : enumWrappers) {
            ret.add(en.enumField);
        }
        return ret;
    }

    /**
     * the default set value method
     * @param enumValue value
     */
    @SuppressWarnings("unchecked")
    public void setEnumList(List<E> enumValue) {
        ArrayList<EnumWrapper<E>> tmp = new ArrayList<>(enumValue.size());
        for (E e : enumValue) {
            tmp.add(new EnumWrapper<E>(e));
        }
        set(tmp);
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
