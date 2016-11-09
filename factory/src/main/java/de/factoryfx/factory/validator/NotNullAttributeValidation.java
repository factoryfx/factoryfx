package de.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;

public class NotNullAttributeValidation implements FactoryStyleValidation {
    private final FactoryBase<?, ?> factoryBase;
    private final Field attributeField;

    public NotNullAttributeValidation(FactoryBase<?, ?> factoryBase, Field attributeField) {
        this.factoryBase = factoryBase;
        this.attributeField = attributeField;
    }

    @Override
    public Optional<String> validateFactory() {
        try {
            if(attributeField.get(factoryBase)==null) {
                return Optional.of("should be not null: "+ factoryBase.getClass().getName()+"#"+attributeField.getName());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}
