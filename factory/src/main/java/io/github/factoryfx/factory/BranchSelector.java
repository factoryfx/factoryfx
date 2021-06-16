package io.github.factoryfx.factory;

import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * create subtree with liveobject to instantiate only part of the tree e.g for test or in the gui
 *
 * @param <R> root
 */
public class BranchSelector<R extends FactoryBase<?,R>> {

    private final R root;
    private final FactoryTreeBuilder<?,R> treeBuilder;

    public BranchSelector(R root, FactoryTreeBuilder<?,R> treeBuilder) {
        this.root = root;
        this.root.internal().finalise();
        this.treeBuilder=treeBuilder;
    }

    public BranchSelector(R root) {
        this.root = root;
        this.root.internal().finalise();
        this.treeBuilder=null;
    }


    public BranchSelector(FactoryTreeBuilder<?,R> treeBuilder) {
        this(treeBuilder.buildTree(),treeBuilder);
    }

    @SuppressWarnings("unchecked")
    public <LB,B extends FactoryBase<LB,R>> Branch<LB,B> select(Class<B> factoryClass, String treeBuilderName){
        if (treeBuilder!=null && treeBuilder.getScope(new FactoryTemplateId<B>(factoryClass,treeBuilderName))!= Scope.SINGLETON){
            throw new IllegalArgumentException("can't select prototype");
        }
        for (FactoryBase<?, R> child : this.root.internal().collectChildrenDeep()) {
            if (matchFactory(child,factoryClass,treeBuilderName)){
                return new Branch<>((B)child);
            }
        }
        return null;
    }


    public <LB,B extends FactoryBase<LB,R>> Branch<LB,B> select(FactoryTemplateId<B> template){
        return select(template.clazz,template.name);
    }

    public <LB,B extends FactoryBase<LB,R>> Branch<LB,B> select(Class<B> factoryClass){
        return select(factoryClass,null);
    }

    private <LB,B extends FactoryBase<LB,?>> boolean matchFactory(FactoryBase<?, R> factory, Class<B> factoryClass, String name){
        if (factory.getClass()!=factoryClass){
            return false;
        }
        String treeBuilderName = factory.internal().getTreeBuilderName();
        if (name==null){
            return true;
        }
        return Objects.equals(name, treeBuilderName);
    }

    @SuppressWarnings("unchecked")
    public <LB,B extends FactoryBase<LB,?>> Set<Branch<LB,B>> selectPrototype(Class<B> factoryClass, String name){
        HashSet<Branch<LB, B>> branches = new HashSet<>();
        for (FactoryBase<?, R> child : this.root.internal().collectChildrenDeep()) {
            if (matchFactory(child,factoryClass,name)){
                branches.add(new Branch<>((B)child));
            }
        }
        return branches;
    }

    public <LB,B extends FactoryBase<LB,?>> Set<Branch<LB,B>> selectPrototype(Class<B> factoryClass){
        return selectPrototype(factoryClass,null);
    }

    public static class Branch< L, B extends FactoryBase<L,?>>{
        private final B branchFactory;

        public Branch(B branchFactory) {
            this.branchFactory = branchFactory;
        }

        public Branch<L, B> start(){
            List<? extends FactoryBase<?, ?>> factoriesInCreateAndStartOrder = branchFactory.internal().getFactoriesInCreateAndStartOrder();
            factoriesInCreateAndStartOrder.stream().forEach(factoryBase -> factoryBase.internal().start());
            return this;
        }

        public Branch<L, B> stop(){
            List<? extends FactoryBase<?, ?>> factoriesInDestroyOrder = branchFactory.internal().getFactoriesInDestroyOrder();
            factoriesInDestroyOrder.stream().forEach(factoryBase -> factoryBase.internal().destroy());
            return this;
        }

        public L instance(){
            return this.branchFactory.internal().instance();
        }

        public Branch<L, B> mock(Function<B,L> mockCreator){
            branchFactory.utility().mock(mockCreator);
            return this;
        }

        public Branch<L, B> mock(L mock){
            branchFactory.utility().mock((f)->mock);
            return this;
        }

        public B factory() {
            return branchFactory;
        }
    }
}
