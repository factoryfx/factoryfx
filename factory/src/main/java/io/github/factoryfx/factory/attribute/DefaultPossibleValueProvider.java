package io.github.factoryfx.factory.attribute;



import io.github.factoryfx.factory.FactoryBase;

import java.util.*;
import java.util.function.Function;

public class DefaultPossibleValueProvider<R extends FactoryBase<?,R>, T extends FactoryBase<?,R>> implements Function<R, Collection<T>> {

    private final Class<T> clazz;

    public DefaultPossibleValueProvider(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<T> apply(R root) {
        List<T> result = new ArrayList<>();
        for (FactoryBase<?,?> factory : root.internal().collectChildrenDeep()) {
            if (clazz.isAssignableFrom(factory.getClass())) {
                result.add((T) factory);
            }
        }
        return result;
    }
}
