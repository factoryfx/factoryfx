package de.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.util.Optional;

import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.factory.FactoryBase;

public class NoReferenceAttribute implements FactoryStyleValidation {
    private final FactoryBase<?, ?> factoryBase;
    private final Field attributeField;

    public NoReferenceAttribute(FactoryBase<?, ?> factoryBase, Field attributeField) {
        this.factoryBase = factoryBase;
        this.attributeField = attributeField;
    }

    @Override
    public Optional<String> validateFactory() {
        if (ReferenceAttribute.class==attributeField.getType()) {
            return Optional.of("should be FactoryReferenceAttribute: "+ factoryBase.getClass().getName()+"#"+attributeField.getName());
        }
        if (ReferenceListAttribute.class==attributeField.getType()) {
            return Optional.of("should be FactoryListReferenceAttribute: "+ factoryBase.getClass().getName()+"#"+attributeField.getName());
        }
        return Optional.empty();
    }
}
