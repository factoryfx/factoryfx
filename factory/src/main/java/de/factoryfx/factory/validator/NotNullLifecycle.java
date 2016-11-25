package de.factoryfx.factory.validator;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;

public class NotNullLifecycle implements FactoryStyleValidation {
    private final FactoryBase<?, ?> factoryBase;

    public NotNullLifecycle(FactoryBase<?, ?> factoryBase) {
        this.factoryBase = factoryBase;
    }

    @Override
    public Optional<String> validateFactory() {
        if(factoryBase.createLifecycleController()==null) {
            return Optional.of("lifecycleControlle be not null: "+ factoryBase.getClass().getName());
        }
        return Optional.empty();
    }
}
