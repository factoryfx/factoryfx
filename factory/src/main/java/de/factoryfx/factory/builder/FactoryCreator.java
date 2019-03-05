package de.factoryfx.factory.builder;

import de.factoryfx.factory.FactoryBase;

import java.util.Map;
import java.util.Objects;
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
        return Objects.equals(this.name,name);
    }

    F factory;
    public F create(FactoryContext<R> context) {
        if (scope==Scope.PROTOTYPE){
            return creator.apply(context);
        } else {
            if (factory==null){
                factory=creator.apply(context);
                factory.internalFactory().setTreeBuilderName(name);
            }
            return factory;
        }

    }

    public Scope getScope() {
        return scope;
    }

    public boolean isEmpty(){
        return factory==null;
    }

    @SuppressWarnings("unchecked")
    public void fillFromExistingFactoryTree(Map<FactoryCreatorIdentifier, FactoryBase<?,?,?>> classToFactory) {
        if (scope==Scope.SINGLETON) {
            factory= (F) classToFactory.get(new FactoryCreatorIdentifier(clazz,name));
        }
    }

    public F createNew(FactoryContext<R> context) {
        factory=creator.apply(context);
        factory.internalFactory().setTreeBuilderName(name);
        return factory;
    }
}