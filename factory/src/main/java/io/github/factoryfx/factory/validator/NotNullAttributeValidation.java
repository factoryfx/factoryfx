package io.github.factoryfx.factory.validator;

import java.util.Optional;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.metadata.FactoryMetadata;

public class NotNullAttributeValidation implements FactoryStyleValidation {
    private final Class<? extends FactoryBase<?,?>> factoryClass;

    public NotNullAttributeValidation(Class<? extends FactoryBase<?,?>> factoryClass) {
        this.factoryClass = factoryClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<String> validateFactory() {
        try {
            new FactoryMetadata(factoryClass); //indirectly calls FactoryMetadata#initAttributeFields
        } catch (IllegalStateException e) {
            return Optional.of(e.getMessage());
        }
        return Optional.empty();
    }
}
