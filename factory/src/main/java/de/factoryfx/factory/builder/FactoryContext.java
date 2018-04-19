package de.factoryfx.factory.builder;

import de.factoryfx.factory.FactoryBase;

import java.util.*;
import java.util.function.Predicate;

public class FactoryContext<R extends FactoryBase<?,?,R>> {

    private List<FactoryCreator<?,R>> factoryCreators = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <L, F extends FactoryBase<?,?,R>> F get(Predicate<FactoryCreator<?,R>> filter){
        Optional<FactoryCreator<?,R>> any = factoryCreators.stream().filter(filter).findAny();
        if (any.isPresent()){
            return (F) any.get().create(this);
        }
        return null;
    }

    public <F extends FactoryBase<?,?,R>> F get(Class<F> clazz){
        return get(fc -> fc.match(clazz));
    }

    public <F extends FactoryBase<?,?,R>> F get(Class<F> clazz, String name){
        return get(fc -> fc.match(clazz) && fc.match(name));
    }

    void addFactoryCreator(FactoryCreator<?,R> factoryCreator){
        factoryCreators.add(factoryCreator);
    }


    @SuppressWarnings("unchecked")
    public <L, F extends FactoryBase<L,?,R>> List<F> getList(Class<F> clazz) {
        ArrayList<F> result = new ArrayList<>();
        factoryCreators.stream().filter(fc -> fc.match(clazz)).forEach(vFactoryCreator -> result.add((F) vFactoryCreator.create(FactoryContext.this)));
        return result;
    }

    public <L, F extends FactoryBase<L,?,R>> boolean anyMatch(Class<F> clazz){
        return factoryCreators.stream().anyMatch(fc -> fc.match(clazz));
    }

    public Scope getScope(Class<?> factoryClazz) {
        Optional<FactoryCreator<?,R>> any = factoryCreators.stream().filter(fc -> fc.match(factoryClazz)).findAny();
        if (any.isPresent()){
            return any.get().getScope();
        }
        return null;
    }
}
