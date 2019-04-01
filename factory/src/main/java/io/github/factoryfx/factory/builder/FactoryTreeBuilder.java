package io.github.factoryfx.factory.builder;


import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.factory.FactoryBase;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/** utility class to build a factory hierarchy(dependency tree)
 *
 *  see RichClientBuilder for an example
 *
 *  It's called tree but really is a DAG (directed acyclic graph). It' called tree to emphasize the main limitation: no cycles.
 *
 * @param <R> root factory
 * */
public class FactoryTreeBuilder<L,R extends FactoryBase<L,R>,S> {
    private final FactoryContext<R> factoryContext = new FactoryContext<>();
    private final Class<R> rootClass;

    public FactoryTreeBuilder(Class<R> rootClass) {
        if (rootClass==null){
            throw new IllegalArgumentException("rootClass is mandatory");

        }
        this.rootClass = rootClass;
    }


    public <F extends FactoryBase<?,R>> void addFactory(Class<F> clazz, Scope scope, Function<FactoryContext<R>, F> creator){
        addFactory(clazz,null,scope,creator);
    }

    public <F extends FactoryBase<?,R>> void addFactory(Class<F> clazz, String name, Scope scope, Function<FactoryContext<R>, F> creator){
        factoryContext.addFactoryCreator(new FactoryCreator<>(clazz,name,scope,creator));
    }


    public <L,F extends FactoryBase<L,R>> void addFactory(Class<F> clazz, Scope scope){
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
        for (FactoryBase<?,?> data : root.internal().collectChildrenDeep()) {
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
    public R buildTreeUnvalidated(){
        R rootFactory = factoryContext.get(rootClass);
        if (rootFactory==null){
            throw new IllegalStateException("FactoryCreator missing for root class "+ rootClass.getSimpleName()+"\n"+"probably missing call: factoryBuilder.addFactory("+rootClass.getSimpleName()+".class,...\n");
        }
        rootFactory.internal().addBackReferences();
        return rootFactory;
    }

    /**
     *  the passed factoryClazz ist created new even if they is declared as Singleton
     *
     * @param factoryClazz factory
     * @param <LO> liveobject
     * @param <FO> factory result
     * @return factory
     */
    public <LO, FO extends FactoryBase<LO,R>> FO buildNewSubTree(Class<FO> factoryClazz){
        return factoryContext.getNew(factoryClazz);
    }

    public <LO, FO extends FactoryBase<LO,R>> FO buildSubTree(Class<FO> factoryClazz){
        return factoryContext.get(factoryClazz);
    }

    public <LO, FO extends FactoryBase<LO,R>> List<FO> buildSubTrees(Class<FO> factoryClazz){
        return factoryContext.getList(factoryClazz);
    }


    public Scope getScope(Class<?> factoryClazz){
        return factoryContext.getScope(factoryClazz);
    }

    public void fillFromExistingFactoryTree(R root) {
        factoryContext.fillFromExistingFactoryTree(root);
    }

    public MicroserviceBuilder<L,R,S> microservice(){
        return new MicroserviceBuilder<>(this.rootClass,this.buildTree(),this, ObjectMapperBuilder.build());
    }

    public MicroserviceBuilder<L,R,S> microservice(SimpleObjectMapper simpleObjectMapper){
        return new MicroserviceBuilder<>(this.rootClass,this.buildTree(),this,simpleObjectMapper);
    }



}
