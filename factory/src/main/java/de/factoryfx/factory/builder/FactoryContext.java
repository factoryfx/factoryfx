package de.factoryfx.factory.builder;

import de.factoryfx.factory.FactoryBase;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class FactoryContext<V> {

    private List<FactoryCreator<V,?,?>> factoryCreators = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <L, F extends FactoryBase<L,V>> F get(Predicate<FactoryCreator<V,?,?>> filter){
        Optional<FactoryCreator<V,?,?>> any = factoryCreators.stream().filter(filter).findAny();
        if (any.isPresent()){
            F factoryBases = (F) any.get().create(this);
            return factoryBases;
        }
        return null;
    }

    public <L, F extends FactoryBase<L,V>> F get(Class<F> clazz){
        return get(fc -> fc.match(clazz));
    }

    public <L, F extends FactoryBase<L,V>> F get(Class<F> clazz, String name){
        return get(fc -> fc.match(clazz) && fc.match(name));
    }

    void addFactoryCreator(FactoryCreator<V,?,?> factoryCreator){
        factoryCreators.add(factoryCreator);
    }


    @SuppressWarnings("unchecked")
    public <L, F extends FactoryBase<L,V>> List<F> getList(Class<F> clazz) {
        ArrayList<F> result = new ArrayList<>();
        factoryCreators.stream().filter(fc -> fc.match(clazz)).forEach(vFactoryCreator -> result.add((F) vFactoryCreator.create(FactoryContext.this)));
        return result;
    }
}
