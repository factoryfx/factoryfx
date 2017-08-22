package de.factoryfx.factory.builder;

import de.factoryfx.factory.FactoryBase;

import java.util.List;
import java.util.Set;

public class SimpleFactoryContext<V> {

    private final Set<Class> stack;
    private final FactoryContext<V> factoryContext;

    public SimpleFactoryContext(FactoryContext<V> factoryContext,Set<Class> stack) {
        this.stack = stack;
        this.factoryContext = factoryContext;
    }

    public <L, F extends FactoryBase<L,V>> F get(Class<F> clazz){
        return factoryContext.get(clazz,stack);
    }

    public <L, F extends FactoryBase<L,V>> List<F> getList(Class<F> clazz) {
        return factoryContext.getList(clazz,stack);
    }
}
