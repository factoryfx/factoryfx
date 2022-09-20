package io.github.factoryfx.factory.builder;

import java.util.Map;
import java.util.function.Function;

import io.github.factoryfx.factory.FactoryBase;

public class FactoryCreator<F extends FactoryBase<?,R>,R extends FactoryBase<?,R>> {
    private final FactoryTemplateId<F> templateId;
    private final Scope scope;
    private final Function<FactoryContext<R>, F> creator;


    public FactoryCreator(FactoryTemplateId<F> templateId, Scope scope, Function<FactoryContext<R>, F> creator) {
        this.templateId = templateId;
        this.scope = scope;
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "FactoryCreator{" + "clazz=" + templateId.clazz + ", name='" + templateId.name + '\'' + '}';
    }

    public boolean match(Class<?> clazzMatch,String name) {
        return templateId.match(clazzMatch, name);
    }

    public boolean match(Class<?> clazzMatch) {
        return templateId.match(clazzMatch);
    }

    public boolean matchLiveObjectClass(Class<?> liveObjectCLass) {
        return templateId.matchLiveObjectCLass(liveObjectCLass);
    }

    public boolean isDuplicate(FactoryCreator<?,R> factoryCreator){
        return templateId.isDuplicate(factoryCreator.templateId);
    }

    private F factory;
    public F create(FactoryContext<R> context) {
        F result;
        if (scope==Scope.PROTOTYPE){
            result = creator.apply(context);
        } else {
            if (factory==null){
                factory=creator.apply(context);
            }
            result=factory;
        }
        templateId.serializeTo(result);
        return result;
    }

    public Scope getScope() {
        return scope;
    }

    public boolean isEmpty(){
        return factory==null;
    }

    @SuppressWarnings("unchecked")
    public void fillFromExistingFactoryTree(Map<FactoryCreatorIdentifier, FactoryBase<?,?>> classToFactory) {
        if (scope==Scope.SINGLETON) {
            factory= (F) classToFactory.get(new FactoryCreatorIdentifier(templateId.clazz,templateId.name));
        }
    }

    public F createNew(FactoryContext<R> context) {
        F factory = creator.apply(context);
        factory.internal().setTreeBuilderName(templateId.name);
        return factory;
    }

    public void reset() {
        factory=null;
    }

    public FactoryCreator<F,R> copy() {
        return new FactoryCreator<>(this.templateId,this.scope,this.creator);
    }
}