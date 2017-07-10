package de.factoryfx.factory.atrribute;

import de.factoryfx.data.Data;
import de.factoryfx.factory.FactoryBase;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public class DefaultPossibleValueProvider<L, T extends FactoryBase<? extends L,?>> implements Function<Data, Collection<T>> {

    private final Class<T> clazz;

    public DefaultPossibleValueProvider(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Collection<T> apply(Data root) {
        Set<T> result = new LinkedHashSet<>();
        for (Data factory : root.internal().collectChildrenDeep()) {
            if (clazz.isAssignableFrom(factory.getClass())) {
                result.add((T) factory);
            }
        }
        return result;
    }
}
