package io.github.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.util.Optional;

import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.FactoryBase;

public class OnlyAttribute implements FactoryStyleValidation {
    private final Class<? extends FactoryBase<?, ?>> factoryClass;
    private final Field attributeField;

    public OnlyAttribute(Class<? extends FactoryBase<?, ?>> factoryClass, Field attributeField) {
        this.factoryClass = factoryClass;
        this.attributeField = attributeField;
    }

    @Override
    public Optional<String> validateFactory() {
        if (!Attribute.class.isAssignableFrom(attributeField.getType())) {
            return Optional.of("Factories should only contains attribute and no state: "+ factoryClass.getName()+"#"+attributeField.getName());
        }
        return Optional.empty();
    }
}
