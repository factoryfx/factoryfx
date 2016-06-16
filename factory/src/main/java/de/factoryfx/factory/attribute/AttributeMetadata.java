package de.factoryfx.factory.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.validation.Validation;

public class AttributeMetadata<T> {
    public LanguageText labelText=new LanguageText();
    public final List<Validation<T>> validations = new ArrayList<>();
    public Optional<Function<FactoryBase<?,?>,T>> possibleValueProviderFromRoot=Optional.empty();


    public AttributeMetadata() {

    }

    private Attribute<T> attribute;
    public AttributeMetadata(Attribute<T> attribute) {
        this.attribute= attribute;
    }
    public Attribute<T> build(){
        return attribute;
    }

    public static class LanguageText{
        private Map<Locale,String> texts=new HashMap<>();

        public String get(Locale locale) {
            return texts.get(locale);
        }

        public void put(Locale locale,String text){
            texts.put(locale,text);
        }
    }
}
