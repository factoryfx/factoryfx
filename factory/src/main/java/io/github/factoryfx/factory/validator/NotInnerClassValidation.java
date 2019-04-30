package io.github.factoryfx.factory.validator;

import io.github.factoryfx.factory.FactoryBase;

import java.lang.reflect.Modifier;
import java.util.Optional;

public class NotInnerClassValidation implements FactoryStyleValidation {
    private final Class<? extends FactoryBase<?,?>> clazz;
    public NotInnerClassValidation(Class<? extends FactoryBase<?,?>> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Optional<String> validateFactory() {
        boolean invalid = clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers());
        if (invalid) {
            return Optional.of(clazz+"is an inner classes can't be Factory classes (Non-static nested classes are called inner classes)");
        } else {
            return Optional.empty();
        }
    }

}
