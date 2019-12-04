package io.github.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Supplier;

import io.github.factoryfx.factory.FactoryBase;

public class NotNullAttributeValidation implements FactoryStyleValidation {
    private final Supplier<? extends FactoryBase<?,?>> factoryBaseSupplier;
    private final Field attributeField;

    public NotNullAttributeValidation(Supplier<? extends FactoryBase<?, ?>> factoryBaseSupplier, Field attributeField) {
        this.factoryBaseSupplier = factoryBaseSupplier;
        this.attributeField = attributeField;
    }

    @Override
    public Optional<String> validateFactory() {
        try {
            FactoryBase<?, ?> factoryBase = this.factoryBaseSupplier.get();
            if(attributeField.get(factoryBase)==null) {
                return Optional.of("Must not be null: "+ factoryBase.getClass().getName()+"#"+attributeField.getName());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}
