package de.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;

public class FinalValidation implements  FactoryStyleValidation {

    private final FactoryBase<?, ?,? > factoryBase;
    private final Field attributeField;

    public FinalValidation(FactoryBase<?, ?, ?> factoryBase, Field attributeField) {
        this.factoryBase = factoryBase;
        this.attributeField = attributeField;
    }

    @Override
    public Optional<String> validateFactory() {
        if((attributeField.getModifiers() & Modifier.FINAL) != java.lang.reflect.Modifier.FINAL) {
            return Optional.of("should be final: "+ factoryBase.getClass().getName()+"#"+attributeField.getName());
        }
        return Optional.empty();
    }
}
