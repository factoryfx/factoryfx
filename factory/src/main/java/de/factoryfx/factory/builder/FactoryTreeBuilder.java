package de.factoryfx.factory.builder;

import de.factoryfx.data.Data;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
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
        if (rootClass==null){
            throw new IllegalArgumentException("rootClass is mandatory");

        }
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

    /**create the complete factory tree that represent the app dependencies and validates the result
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
            throw new IllegalStateException("\n    Factory tree contains validation errors:\n        --------------------------------\n"+
                    validationErrors.stream().map(ValidationError::getSimpleErrorDescription).collect(Collectors.joining( "\n        --------------------------------\n"))+
                    "\n        --------------------------------"
                    );
        }
    }

    /**create the complete factory tree that represent the app dependencies
     * @return dependency tree
     * */
    @SuppressWarnings("unchecked")
    public R buildTreeUnvalidated(){
        R factoryBases = factoryContext.get(rootClass);
        if (factoryBases==null){
            throw new IllegalStateException("FactoryCreator missing for root class "+ rootClass.getSimpleName()+"\n"+"probably missing call: factoryBuilder.addFactory("+rootClass.getSimpleName()+".class,...\n");
        }
        FactoryTreeBuilderBasedAttributeSetup<R> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(this);
        factoryBases.internalFactory().setAttributeSetupHelper(factoryTreeBuilderBasedAttributeSetup);
        return factoryBases.internal().addBackReferences();
    }

    public <L, F extends FactoryBase<L,?,R>> F buildSubTree(Class<F> factoryClazz){
        return factoryContext.get(factoryClazz);
    }

    public Scope getScope(Class<?> factoryClazz){
        return factoryContext.getScope(factoryClazz);
    }
}
