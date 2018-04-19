package de.factoryfx.factory.builder;

import de.factoryfx.factory.FactoryBase;

import java.util.function.Function;

public class FactoryCreator<F extends FactoryBase<?,?,R>,R extends FactoryBase<?,?,R>> {
    private final Class<F> clazz;
    private final Scope scope;
    private final Function<FactoryContext<R>, F> creator;
    private final String name;

    public FactoryCreator(Class<F> clazz,String name, Scope scope, Function<FactoryContext<R>, F> creator) {
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
    public F create(FactoryContext<R> context) {
        if (scope==Scope.PROTOTYPE){
            return creator.apply(context);
        } else {
            if (factory==null){
                factory=creator.apply(context);
            }
            return factory;
        }

    }

    public Scope getScope() {
        return scope;
    }
}