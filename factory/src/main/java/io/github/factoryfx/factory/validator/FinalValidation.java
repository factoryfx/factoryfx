package io.github.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

import io.github.factoryfx.factory.FactoryBase;

public class FinalValidation implements  FactoryStyleValidation {

    private final Class<? extends FactoryBase<?,?>> factoryClass;
    private final Field attributeField;

    public FinalValidation(Class<? extends FactoryBase<?,?>> factoryClass, Field attributeField) {
        this.factoryClass = factoryClass;
        this.attributeField = attributeField;
    }

    @Override
    public Optional<String> validateFactory() {
        if((attributeField.getModifiers() & Modifier.FINAL) != java.lang.reflect.Modifier.FINAL) {
            return Optional.of("Should be final: "+ factoryClass.getName()+"#"+attributeField.getName());
        }
        return Optional.empty();
    }
}
