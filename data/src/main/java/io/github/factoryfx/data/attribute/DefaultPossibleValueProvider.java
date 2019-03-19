package io.github.factoryfx.data.attribute;

import io.github.factoryfx.data.Data;

import java.util.*;
import java.util.function.Function;

public class DefaultPossibleValueProvider<L, T extends Data> implements Function<Data, Collection<T>> {

    private final Class<T> clazz;

    public DefaultPossibleValueProvider(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<T> apply(Data root) {
        List<T> result = new ArrayList<>();
        for (Data factory : root.internal().collectChildrenDeep()) {
            if (clazz.isAssignableFrom(factory.getClass())) {
                result.add((T) factory);
            }
        }
        return result;
    }
}
