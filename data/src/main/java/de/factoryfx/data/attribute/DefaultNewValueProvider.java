package de.factoryfx.data.attribute;

import de.factoryfx.data.Data;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class DefaultNewValueProvider<L, T extends Data> implements Function<Data, T> {

    private final Class<T> clazz;

    public DefaultNewValueProvider(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T apply(Data root) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
