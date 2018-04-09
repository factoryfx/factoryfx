package de.factoryfx.factory.builder;

import de.factoryfx.data.Data;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.factory.FactoryBase;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/** utility class to build a factory hierarchy
 *
 *  see RichClientBuilder for an example
 *
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

    /**create the complete factory tree that represent teh app dependencies
     * @return dependency tree
     * */
    public R buildTree(){
        R root = buildTreeUnvalidated();
        validate(root);
        return root;
    }

    private void validate(R root) {
        List<ValidationError> validationErrors=new ArrayList<>();
        for (Data data : root.internal().collectChildrenDeep()) {
            validationErrors.addAll(data.internal().validateFlat());
        }
        if (!validationErrors.isEmpty()){
            throw new IllegalStateException("factory tree contains validation errors:\n"+validationErrors.stream().map(ValidationError::getSimpleErrorDescription).collect(Collectors.joining( "\n" )));
        }
    }

    /**create the complete factory tree that represent teh app dependencies
     * @return dependency tree
     * */
    public R buildTreeUnvalidated(){
        R factoryBases = factoryContext.get(rootClass);
        if (factoryBases==null){
            throw new IllegalStateException("FactoryCreator missing for root class"+ rootClass);
        }
        R root = factoryBases.internal().prepareUsableCopy();
        return root;
    }

    public <L, F extends FactoryBase<L,V>> F buildSubTree(Class<F> factoryClazz){
        return factoryContext.get(factoryClazz);
    }
}
