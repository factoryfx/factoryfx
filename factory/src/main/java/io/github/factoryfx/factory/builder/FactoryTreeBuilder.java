package io.github.factoryfx.factory.builder;


import io.github.factoryfx.factory.BranchSelector;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.factory.FactoryBase;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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
public class FactoryTreeBuilder<L,R extends FactoryBase<L,R>> {
    private final FactoryContext<R> factoryContext = new FactoryContext<>();
    private final Class<R> rootClass;

    public FactoryTreeBuilder(Class<R> rootClass) {
        this(rootClass,new DefaultCreator<>(rootClass));
    }

    public FactoryTreeBuilder(Class<R> rootClass, Function<FactoryContext<R>, R> creator) {
        if (rootClass==null){
            throw new IllegalArgumentException("rootClass is mandatory");

        }
        this.rootClass = rootClass;
        addFactory(rootClass,Scope.SINGLETON,creator);
    }


    public <F extends FactoryBase<?,R>> void addFactory(Class<F> clazz, Scope scope, Function<FactoryContext<R>, F> creator){
        addFactory(clazz,null,scope,creator);
    }

    public <F extends FactoryBase<?,R>> void addFactory(Class<F> clazz, String name, Scope scope, Function<FactoryContext<R>, F> creator){
        factoryContext.addFactoryCreator(new FactoryCreator<>(clazz,name,scope,creator));
    }


    public <LO,F extends FactoryBase<LO,R>> void addFactory(Class<F> clazz, Scope scope){
        addFactory(clazz,scope,new DefaultCreator<>(clazz));
    }

    /**create the complete factory tree that represent the app dependencies and validates the result<br>
     * the tree is only created once per builder, multiple buildTree calls return the same result
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

    private R rootFactory;
    /**create the complete factory tree that represent the app dependencies
     * @return dependency tree
     * */
    public R buildTreeUnvalidated(){
        if (rootFactory!=null) {
            return rootFactory;
        }
        this.rootFactory = factoryContext.get(rootClass);
        if (rootFactory==null){
            throw new IllegalStateException("FactoryCreator missing for root class "+ rootClass.getSimpleName()+"\n"+"probably missing call: factoryBuilder.addFactory("+rootClass.getSimpleName()+".class,...\n");
        }
        rootFactory.internal().finalise();
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

    public <LO, FO extends FactoryBase<LO,R>> List<FO> buildSubTrees(Class<FO> factoryClazz){
        return factoryContext.getList(factoryClazz);
    }

    /**
     * indented use is for testing to create branches and set mocks
     * @return BranchSelector
     */
    public BranchSelector<R> branch(){
        return new BranchSelector<>(this);
    }


    public Scope getScope(Class<?> factoryClazz){
        return factoryContext.getScope(factoryClazz);
    }

    public void fillFromExistingFactoryTree(R root) {
        factoryContext.fillFromExistingFactoryTree(root);
    }

    public MicroserviceBuilder<L,R> microservice(){
        return new MicroserviceBuilder<>(this.rootClass,this.buildTree(),this, ObjectMapperBuilder.build());
    }

    public MicroserviceBuilder<L,R> microservice(SimpleObjectMapper simpleObjectMapper){
        return new MicroserviceBuilder<>(this.rootClass,this.buildTree(),this,simpleObjectMapper);
    }

    /**
     * set mocks for factories
     * @param mocker mocker
     * @return MicroserviceBuilder
     */
    public MicroserviceBuilder<L,R> microservice(Consumer<R> mocker){
        R root = this.buildTree();
        mocker.accept(root);
        return new MicroserviceBuilder<>(this.rootClass, root,this,ObjectMapperBuilder.build());
    }


}
