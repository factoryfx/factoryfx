package io.github.factoryfx.factory.attribute;



import io.github.factoryfx.factory.FactoryBase;

import java.util.*;
import java.util.function.Function;

public class DefaultPossibleValueProvider<T extends FactoryBase<?,?>> implements Function<FactoryBase<?,?>, Collection<T>> {

    private final Class<T> clazz;

    public DefaultPossibleValueProvider(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<T> apply(FactoryBase<?,?> root) {
        List<T> result = new ArrayList<>();
        for (FactoryBase<?,?> factory : root.internal().collectChildrenDeep()) {
            if (clazz.isAssignableFrom(factory.getClass())) {
                result.add((T) factory);
            }
        }
        return result;
    }
}
