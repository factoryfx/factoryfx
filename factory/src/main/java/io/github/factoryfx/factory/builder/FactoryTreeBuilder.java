package io.github.factoryfx.factory.builder;


import io.github.factoryfx.factory.BranchSelector;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.factory.FactoryBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    protected final FactoryTemplateId<R> rootTemplateId;

    protected FactoryTreeBuilder(FactoryTemplateId<R> rootTemplateId, Consumer<FactoryTreeBuilder<L,R>> rootTemplateAdder) {
        this.rootTemplateId = rootTemplateId;
        rootTemplateAdder.accept(this);
    }

    public FactoryTreeBuilder(FactoryTemplateId<R> rootTemplateId, Function<FactoryContext<R>, R> creator) {
        this(rootTemplateId, builder -> {
            builder.addFactory(rootTemplateId, Scope.SINGLETON, creator);
        });
    }

    public FactoryTreeBuilder(Class<R> rootClass) {
        this(rootClass,new DefaultCreator<>(rootClass));
    }

    public FactoryTreeBuilder(Class<R> rootClass, Function<FactoryContext<R>, R> creator) {
        this(new FactoryTemplateId<>(rootClass,null),creator);
        if (rootClass==null){
            throw new IllegalArgumentException("rootClass is mandatory");
        }
    }

    public <F extends FactoryBase<?,R>> void addFactory(FactoryTemplateId<F> templateId, Scope scope, Function<FactoryContext<R>, F> creator){
        factoryContext.addFactoryCreator(new FactoryCreator<>(templateId,scope,creator));
    }

    public <F extends FactoryBase<?,R>> void addFactory(Class<F> clazz, Scope scope, Function<FactoryContext<R>, F> creator){
        addFactory(new FactoryTemplateId<>(clazz,null),scope,creator);
    }

    public <F extends FactoryBase<?,R>> void addFactory(String name, Scope scope, Function<FactoryContext<R>, F> creator){
        addFactory(new FactoryTemplateId<>(null,name),scope,creator);
    }

    public <F extends FactoryBase<?,R>> void addFactory(Class<F> clazz, String name, Scope scope, Function<FactoryContext<R>, F> creator){
        addFactory(new FactoryTemplateId<>(clazz,name),scope,creator);
    }

    public <F extends FactoryBase<?,R>> void addFactory(Class<F> clazz, Scope scope){
        addFactory(clazz,scope,new DefaultCreator<>(clazz));
    }


    public <F extends FactoryBase<?,R>> void addSingleton(FactoryTemplateId<F> templateId, Function<FactoryContext<R>, F> creator){
        factoryContext.addFactoryCreator(new FactoryCreator<>(templateId,Scope.SINGLETON,creator));
    }

    public <F extends FactoryBase<?,R>> void addSingleton(Class<F> clazz, Function<FactoryContext<R>, F> creator){
        addSingleton(new FactoryTemplateId<>(clazz,null),creator);
    }

    public <F extends FactoryBase<?,R>> void addSingleton(String name, Function<FactoryContext<R>, F> creator){
        addSingleton(new FactoryTemplateId<>(null,name),creator);
    }

    public <F extends FactoryBase<?,R>> void addSingleton(Class<F> clazz, String name, Function<FactoryContext<R>, F> creator){
        addSingleton(new FactoryTemplateId<>(clazz,name),creator);
    }

    public <F extends FactoryBase<?,R>> void addSingleton(Class<F> clazz, String name){
        addSingleton(new FactoryTemplateId<>(clazz,name),new DefaultCreator<>(clazz));
    }

    public <F extends FactoryBase<?,R>> void addSingleton(Class<F> clazz){
        addSingleton(new FactoryTemplateId<>(clazz,null),new DefaultCreator<>(clazz));
    }



    public <F extends FactoryBase<?,R>> void addPrototype(FactoryTemplateId<F> templateId, Function<FactoryContext<R>, F> creator){
        factoryContext.addFactoryCreator(new FactoryCreator<>(templateId,Scope.PROTOTYPE,creator));
    }

    public <F extends FactoryBase<?,R>> void addPrototype(Class<F> clazz, Function<FactoryContext<R>, F> creator){
        addPrototype(new FactoryTemplateId<>(clazz,null),creator);
    }

    public <F extends FactoryBase<?,R>> void addPrototype(String name, Function<FactoryContext<R>, F> creator){
        addPrototype(new FactoryTemplateId<>(null,name),creator);
    }

    public <F extends FactoryBase<?,R>> void addPrototype(Class<F> clazz, String name, Function<FactoryContext<R>, F> creator){
        addPrototype(new FactoryTemplateId<>(clazz,name),creator);
    }

    public <F extends FactoryBase<?,R>> void addPrototype(Class<F> clazz, String name){
        addPrototype(new FactoryTemplateId<>(clazz,name),new DefaultCreator<>(clazz));
    }

    public <F extends FactoryBase<?,R>> void addPrototype(Class<F> clazz){
        addPrototype(new FactoryTemplateId<>(clazz,null),new DefaultCreator<>(clazz));
    }


    /**
     * workaround for factories with generic Parameter e.g. FactoryX{@literal <}R{@literal >},  used in combination with {@link FactoryContext#getUnsafe(Class)}
     * @param templateId templateId
     * @param scope scope
     * @param creator creator
     * @param <F> Factory
     */
    @SuppressWarnings("unchecked")
    public <F extends FactoryBase<?,R>> void addFactoryUnsafe(FactoryTemplateId<?> templateId, Scope scope, Function<FactoryContext<R>, F> creator){
        factoryContext.addFactoryCreator(new FactoryCreator<>((FactoryTemplateId<F>) templateId,scope,creator));
    }

    /**
     * @see #addFactoryUnsafe(FactoryTemplateId, Scope, Function)
     * @param clazz clazz
     * @param scope scope
     * @param creator creator
     * @param <F> factory
     */
    @SuppressWarnings("unchecked")
    public <F extends FactoryBase<?,R>> void addFactoryUnsafe(Class<?> clazz, Scope scope, Function<FactoryContext<R>, F> creator){
        addFactoryUnsafe(new FactoryTemplateId(clazz,null),scope,creator);
    }

    /**
     * @see #addFactoryUnsafe(FactoryTemplateId, Scope, Function)
     * @param name name
     * @param scope scope
     * @param creator creator
     * @param <F> factory
     */
    public <F extends FactoryBase<?,R>> void addFactoryUnsafe(String name, Scope scope, Function<FactoryContext<R>, F> creator){
        addFactoryUnsafe(new FactoryTemplateId<>(null,name),scope,creator);
    }

    /**
     * @see #addFactoryUnsafe(FactoryTemplateId, Scope, Function)
     * @param clazz clazz
     * @param name name
     * @param scope scope
     * @param creator creator
     * @param <F> factory
     */
    @SuppressWarnings("unchecked")
    public <F extends FactoryBase<?,R>> void addFactoryUnsafe(Class<?> clazz, String name, Scope scope, Function<FactoryContext<R>, F> creator){
        addFactoryUnsafe(new FactoryTemplateId(clazz,name),scope,creator);
    }

    /**
     * @see #addFactoryUnsafe(FactoryTemplateId, Scope, Function)
     * @param clazz clazz
     * @param scope scope
     */
    @SuppressWarnings("unchecked")
    public void addFactoryUnsafe(Class<?> clazz, Scope scope){
        addFactoryUnsafe(clazz,scope,new DefaultCreator(clazz));
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

    boolean templateValidation=true;

    /**
     *  disable templateId validation, The validation ensures that all factories have a template in the builder.
     *  This is important for migration of persistent factory configurations which is no need for non persistent factories
     *  */
    public void markAsNonPersistentFactoryBuilder() {
        templateValidation=false;
    }

    public boolean isPersistentFactoryBuilder() {
        return templateValidation;
    }



    private void validate(R root) {
        List<ValidationError> validationErrors=new ArrayList<>();
        for (FactoryBase<?,?> factory : root.internal().collectChildrenDeep()) {
            validationErrors.addAll(factory.internal().validateFlat());
            if (templateValidation && !factory.internal().isCreatedWithBuilderTemplate() && !(factory.internal().attributeList().isEmpty())){
                throw new IllegalStateException("\nFactory not created with template\nfix: factory.refAttribute.set(ctx.get("+factory.getClass().getSimpleName()+".class)) instead of factory.refAttribute.set(new "+factory.getClass().getSimpleName()+"())\nthe validation can be disabled with FactoryTreeBuilder#markAsNonPersistentFactoryBuilder())\n\n"+factory.internal().debugInfo());
            }
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
        factoryContext.reset();
        if (rootFactory!=null) {
            return rootFactory;
        }
        for (Function<FactoryContext<R>, NestedBuilder<R>> customBuildersCreator : customBuildersCreators) {
            NestedBuilder<R> nestedBuilder = customBuildersCreator.apply(factoryContext);
            nestedBuilder.internal_build(this);
        }
        customBuildersCreators.clear();//only add once
        this.rootFactory = factoryContext.get(rootTemplateId);
        if (rootFactory==null){
            throw new IllegalStateException("FactoryCreator missing for root class "+ rootTemplateId.clazz);
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
        return new MicroserviceBuilder<>(this.rootTemplateId.clazz,this.buildTree(),this, ObjectMapperBuilder.build());
    }

    public MicroserviceBuilder<L,R> microservice(SimpleObjectMapper simpleObjectMapper){
        return new MicroserviceBuilder<>(this.rootTemplateId.clazz,this.buildTree(),this,simpleObjectMapper);
    }

    /**
     * set mocks for factories
     * @param mocker mocker
     * @return MicroserviceBuilder
     */
    public MicroserviceBuilder<L,R> microservice(Consumer<R> mocker){
        R root = this.buildTree();
        mocker.accept(root);
        return new MicroserviceBuilder<>(this.rootTemplateId.clazz, root,this,ObjectMapperBuilder.build());
    }


    private static class RebuildGroup {
        List<FactoryBase<?, ?>> oldBuild=new ArrayList<>();
        List<FactoryBase<?, ?>> newBuild=new ArrayList<>();
    }

    public boolean isRebuildAble(List<FactoryBase<?, R>> existingFactoryBases){
        for (FactoryBase<?, R> factory : existingFactoryBases) {
            if (factory.internal().getTreeBuilderName()==null &&  !factory.internal().isTreeBuilderClassUsed()){
                return false;
            }
        }
        return true;
    }

    public R rebuildTreeUnvalidated(List<FactoryBase<?, R>> existingFactoryBases) {
        factoryContext.reset();
        rootFactory=null;
        R rootRebuild = this.buildTreeUnvalidated();

        Map<FactoryTemplateId,RebuildGroup> templateToGroup = new HashMap<>();
        for (FactoryBase<?, R> factory : existingFactoryBases) {
            FactoryTemplateId<? extends FactoryBase<?, R>> factoryBaseFactoryTemplateId = new FactoryTemplateId<>(factory);
            if (!templateToGroup.containsKey(factoryBaseFactoryTemplateId)){
                templateToGroup.put(factoryBaseFactoryTemplateId,new RebuildGroup());
            }
            templateToGroup.get(factoryBaseFactoryTemplateId).oldBuild.add(factory);
        }

        for (FactoryBase<?, R> factory : rootRebuild.internal().collectChildrenDeep()) {
            FactoryTemplateId<? extends FactoryBase<?, R>> factoryBaseFactoryTemplateId = new FactoryTemplateId<>(factory);
            if (!templateToGroup.containsKey(factoryBaseFactoryTemplateId)){
                templateToGroup.put(factoryBaseFactoryTemplateId,new RebuildGroup());
            }
            templateToGroup.get(factoryBaseFactoryTemplateId).newBuild.add(factory);
        }

        for (Map.Entry<FactoryTemplateId, RebuildGroup> entry: templateToGroup.entrySet()) {
            RebuildGroup group = entry.getValue();
            if (group.oldBuild.size()==1 && group.newBuild.size()==1){
                group.newBuild.get(0).setId(group.oldBuild.get(0).getId());
            }

            if (group.oldBuild.size()>1 && group.newBuild.size()>1 && group.oldBuild.size()==group.newBuild.size()){
                for (int i = 0; i < group.oldBuild.size(); i++) { //TODO this is order dependent
                    group.newBuild.get(i).setId(group.oldBuild.get(i).getId());
                }
            }
        }

        return rootRebuild;
    }



    List<Function<FactoryContext<R>, NestedBuilder<R>>> customBuildersCreators= new ArrayList<>();

    public void addBuilder(Function<FactoryContext<R>, NestedBuilder<R>> builderCreators) {
        customBuildersCreators.add(builderCreators);
    }
}
