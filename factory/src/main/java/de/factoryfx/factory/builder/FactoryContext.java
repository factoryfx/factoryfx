package de.factoryfx.factory.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import de.factoryfx.factory.FactoryBase;

public class FactoryContext<R extends FactoryBase<?,?,R>> {

    private List<FactoryCreator<?,R>> factoryCreators = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <L, F extends FactoryBase<?,?,R>> F get(Predicate<FactoryCreator<?,R>> filter){
        return factoryCreators.stream().filter(filter).findAny().map(rFactoryCreator -> (F) rFactoryCreator.create(this)).orElse(null);
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
        return factoryCreators.stream().filter(fc -> fc.match(factoryClazz)).findAny().map(FactoryCreator::getScope).orElse(null);
    }
}
