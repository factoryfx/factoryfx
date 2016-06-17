package de.factoryfx.factory.attribute.builder;

import java.util.Locale;

import de.factoryfx.factory.attribute.Attribute;
import de.factoryfx.factory.validation.Validation;

public class AttributeMetadataBuilder<T,A extends Attribute<T>> {
    private final A attribute;

    public AttributeMetadataBuilder(A attribute) {
        this.attribute=attribute;
    }

    public AttributeMetadataBuilder<T,A> validation(Validation<T> validation){
        attribute.metadata.validations.add(validation);
        return this;
    }

    public AttributeMetadataBuilder<T,A> labelText(String labelText){
        attribute.metadata.labelText.put(Locale.ENGLISH,labelText);
        return this;
    }

    public AttributeMetadataBuilder<T,A> labelText(String labelText, Locale locale){
        attribute.metadata.labelText.put(locale,labelText);
        return this;
    }

    public AttributeMetadataBuilder<T,A> de(String labelText){
        attribute.metadata.labelText.put(Locale.GERMAN,labelText);
        return this;
    }

    public AttributeMetadataBuilder<T,A> en(String labelText){
        attribute.metadata.labelText.put(Locale.ENGLISH,labelText);
        return this;
    }
    
    public A build(){
        return attribute;
    }
}
