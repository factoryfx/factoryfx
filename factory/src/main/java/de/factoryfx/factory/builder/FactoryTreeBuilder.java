package de.factoryfx.factory.builder;

import de.factoryfx.factory.FactoryBase;

import java.util.function.Function;

/** utility class to build a factory hierarchy
 * @param <V>  vistor
 * @param <RL> root liveobject
 * @param <R> root factory
 * */
public class FactoryTreeBuilder<V, RL, R extends FactoryBase<RL,V>> {
    private final FactoryContext<V> factoryContext = new FactoryContext<>();
    private final Class<R> rootClass;

    public FactoryTreeBuilder(Class<R> rootClass) {
        this.rootClass = rootClass;
    }


    public <L, F extends FactoryBase<L,V>> void addFactory(Class<F> clazz, Scope scope, Function<FactoryContext<V>, F> creator){
        addFactory(clazz,"",scope,creator);
    }

    public <L, F extends FactoryBase<L,V>> void addFactory(Class<F> clazz, String name, Scope scope, Function<FactoryContext<V>, F> creator){
        factoryContext.addFactoryCreator(new FactoryCreator<>(clazz,name,scope,creator));
    }


    public <L, F extends FactoryBase<L,V>> void addFactory(Class<F> clazz, Scope scope){
        addFactory(clazz,scope,new DefaultCreator<>(clazz));
    }

    public R buildTree(){
        R factoryBases = factoryContext.get(rootClass);
        if (factoryBases==null){
            throw new IllegalStateException("FactoryCreator missing for root class"+ rootClass);
        }
        return factoryBases.internal().prepareUsableCopy();
    }

    public <L, F extends FactoryBase<L,V>> F buildSubTree(Class<F> factoryClazz){
        return factoryContext.get(factoryClazz);
    }
}
