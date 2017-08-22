package de.factoryfx.factory.builder;

import de.factoryfx.factory.FactoryBase;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class FactoryCreator<V, L, F extends FactoryBase<L,V>> {
    private final Class<F> clazz;
    private final Scope scope;
    private final Function<SimpleFactoryContext<V>, F> creator;

    public FactoryCreator(Class<F> clazz, Scope scope, Function<SimpleFactoryContext<V>, F> creator) {
        this.clazz = clazz;
        this.scope = scope;
        this.creator = creator;
    }

    public boolean match(Class<?> clazzMatch) {
        return clazz==clazzMatch;
    }

    F factory;
    public F create(SimpleFactoryContext<V> context) {
        if (scope==Scope.PROTOTYPE){
            return creator.apply(context);
        } else {
            if (factory==null){
                factory=creator.apply(context);
            }
            return factory;
        }

    }
}