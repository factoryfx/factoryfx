package de.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;

public class PublicValidation implements  FactoryStyleValidation {
    private final FactoryBase<?, ?> factoryBase;
    private final Field attributeField;

    public PublicValidation(FactoryBase<?, ?> factoryBase, Field attributeField) {
        this.factoryBase = factoryBase;
        this.attributeField = attributeField;
    }

    @Override
    public Optional<String> validateFactory() {
        if((attributeField.getModifiers() & Modifier.PUBLIC) != java.lang.reflect.Modifier.PUBLIC) {
            return Optional.of("should be public: "+ factoryBase.getClass().getName()+"#"+attributeField.getName());
        }
        return Optional.empty();
    }
}
