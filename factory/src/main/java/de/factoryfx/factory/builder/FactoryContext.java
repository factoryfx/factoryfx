package de.factoryfx.factory.builder;

import de.factoryfx.factory.FactoryBase;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class FactoryContext<V> {

    private List<FactoryCreator<V,?,?>> factoryCreators = new ArrayList<>();

    public <L, F extends FactoryBase<L,V>> F get(Class<F> clazz, Set<Class> stack){
        Optional<FactoryCreator<V,?,?>> any = factoryCreators.stream().filter(fc -> fc.match(clazz)).findAny();
        if (any.isPresent()){

            if (!stack.add(clazz)){
                return null;
            } else {
                F factoryBases = (F) any.get().create(new SimpleFactoryContext<>(this,stack));
                stack.remove(clazz);
                return factoryBases;
            }
        }
        return null;
    }

    public void addFactoryCreator(FactoryCreator<V,?,?> factoryCreator){
        factoryCreators.add(factoryCreator);
    }


    public <L, F extends FactoryBase<L,V>> List<F> getList(Class<F> clazz, Set<Class> stack) {
        ArrayList<F> result = new ArrayList<>();
        factoryCreators.stream().filter(fc -> fc.match(clazz)).forEach(vFactoryCreator -> result.add((F) vFactoryCreator.create(new SimpleFactoryContext<V>(FactoryContext.this,stack))));
        return result;
    }
}
