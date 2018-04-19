package de.factoryfx.factory.builder;

import de.factoryfx.data.Data;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.factory.AttributeSetupHelper;
import de.factoryfx.factory.FactoryBase;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/** utility class to build a factory hierarchy
 *
 *  see RichClientBuilder for an example
 *
 * @param <R> root factory
 * */
public class FactoryTreeBuilder<R extends FactoryBase<?,?,R>> {
    private final FactoryContext<R> factoryContext = new FactoryContext<>();
    private final Class<R> rootClass;

    public FactoryTreeBuilder(Class<R> rootClass) {
        this.rootClass = rootClass;
    }


    public <F extends FactoryBase<?,?,R>> void addFactory(Class<F> clazz, Scope scope, Function<FactoryContext<R>, F> creator){
        addFactory(clazz,"",scope,creator);
    }

    public <F extends FactoryBase<?,?,R>> void addFactory(Class<F> clazz, String name, Scope scope, Function<FactoryContext<R>, F> creator){
        factoryContext.addFactoryCreator(new FactoryCreator<>(clazz,name,scope,creator));
    }


    public <F extends FactoryBase<?,?,R>> void addFactory(Class<F> clazz, Scope scope){
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
    @SuppressWarnings("unchecked")
    public R buildTreeUnvalidated(){
        R factoryBases = factoryContext.get(rootClass);
        if (factoryBases==null){
            throw new IllegalStateException("FactoryCreator missing for root class"+ rootClass);
        }
        AttributeSetupHelper<R> attributeSetupHelper = new AttributeSetupHelper<>(this);
        factoryBases.internalFactory().setAttributeSetupHelper(attributeSetupHelper);
        return factoryBases.internal().prepareUsableCopy(null, attributeSetupHelper);
    }

    public <L, F extends FactoryBase<L,?,R>> F buildSubTree(Class<F> factoryClazz){
        return factoryContext.get(factoryClazz);
    }

    public Scope getScope(Class<?> factoryClazz){
        return factoryContext.getScope(factoryClazz);
    }
}
