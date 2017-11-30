package de.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.util.Optional;

public class NoIdAsAttributeName implements FactoryStyleValidation {
    private final Field attributeField;

    public NoIdAsAttributeName(Field attributeField) {
        this.attributeField = attributeField;
    }

    @Override
    public Optional<String> validateFactory() {
        boolean valid = !attributeField.getName().equals("id");
        if (!valid) {
            return Optional.of("id  is not a valif attribute name cause name conflict in json");
        } else {
            return Optional.empty();
        }
    }

}
