package de.factoryfx.data.attribute;

import de.factoryfx.data.Data;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public class DefaultNewValueProvider<L, T extends Data> implements Function<Data, T> {

    private final Class<T> clazz;

    public DefaultNewValueProvider(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T apply(Data root) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
