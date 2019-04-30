package io.github.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

import io.github.factoryfx.factory.FactoryBase;

public class PublicValidation implements  FactoryStyleValidation {
    private final Class<? extends FactoryBase<?,?>> factoryClass;
    private final Field attributeField;

    public PublicValidation(Class<? extends FactoryBase<?,?>> factoryClass, Field attributeField) {
        this.factoryClass = factoryClass;
        this.attributeField = attributeField;
    }

    @Override
    public Optional<String> validateFactory() {
        if((attributeField.getModifiers() & Modifier.PUBLIC) != java.lang.reflect.Modifier.PUBLIC) {
            return Optional.of("should be public: "+ factoryClass.getName()+"#"+attributeField.getName());
        }
        return Optional.empty();
    }
}
