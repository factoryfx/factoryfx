package io.github.factoryfx.factory.attribute;

import io.github.factoryfx.factory.FactoryBase;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class DefaultNewValueProvider<R extends FactoryBase<?,R>,T extends FactoryBase<?,R>> implements Function<R, T> {

    private final Class<T> clazz;

    public DefaultNewValueProvider(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T apply(R root) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
