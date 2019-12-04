package io.github.factoryfx.factory.validator;

import java.lang.reflect.Modifier;
import java.util.Optional;

import io.github.factoryfx.factory.FactoryBase;

public class NotInnerClassValidation implements FactoryStyleValidation {
    private final Class<? extends FactoryBase<?,?>> clazz;
    public NotInnerClassValidation(Class<? extends FactoryBase<?,?>> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Optional<String> validateFactory() {
        boolean invalid = clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers());
        if (invalid) {
            return Optional.of(clazz+" is an inner class, which must not be a Factory class (Non-static nested classes are called inner classes)");
        } else {
            return Optional.empty();
        }
    }

}
