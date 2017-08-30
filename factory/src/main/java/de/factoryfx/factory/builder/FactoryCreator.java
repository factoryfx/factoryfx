package de.factoryfx.factory.builder;

import de.factoryfx.factory.FactoryBase;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class FactoryCreator<V, L, F extends FactoryBase<L,V>> {
    private final Class<F> clazz;
    private final Scope scope;
    private final Function<FactoryContext<V>, F> creator;
    private final String name;

    public FactoryCreator(Class<F> clazz,String name, Scope scope, Function<FactoryContext<V>, F> creator) {
        this.clazz = clazz;
        this.scope = scope;
        this.creator = creator;
        this.name=name;
    }

    public boolean match(Class<?> clazzMatch) {
        return clazz==clazzMatch;
    }

    public boolean match(String name) {
        return this.name.equals(name);
    }

    F factory;
    public F create(FactoryContext<V> context) {
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