package io.github.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Supplier;

import io.github.factoryfx.factory.FactoryBase;

public class NotNullAttributeValidation implements FactoryStyleValidation {
    private final Supplier<? extends FactoryBase<?,?>> factoryBaseSupplier;

    public NotNullAttributeValidation(Supplier<? extends FactoryBase<?, ?>> factoryBaseSupplier) {
        this.factoryBaseSupplier = factoryBaseSupplier;
    }

    @Override
    public Optional<String> validateFactory() {
        try {
            FactoryBase<?, ?> factoryBase = this.factoryBaseSupplier.get();
        } catch (IllegalStateException e) {
            return Optional.of(e.getMessage());
        }
        return Optional.empty();
    }
}
