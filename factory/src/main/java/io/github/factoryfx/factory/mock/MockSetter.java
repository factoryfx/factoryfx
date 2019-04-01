package io.github.factoryfx.factory.mock;

import io.github.factoryfx.factory.FactoryBase;

/** utiliy for testing
 *
 * set mock for factory based on class or builderName
 * @param <R> root
 */
public class MockSetter<R extends FactoryBase<?,R>> {
    private final  FactoryBase<?,R> root;

    public MockSetter(FactoryBase<?,R> root){
        this.root=root;
    }

    @SuppressWarnings("unchecked")
    public <L,M extends FactoryBase<L,R>> void setMock(Class<M> clazz, L mock){
        for (FactoryBase<?, R> child : root.internal().collectChildrenDeep()) {
            if (child.getClass()==clazz){
                ((M)child).utility().mock((f)->mock);
            }
        }

    }

    @SuppressWarnings("unchecked")
    public <L,M extends FactoryBase<L,R>> void setMock(Class<M> clazz, String builderName, L mock){
        for (FactoryBase<?, R> child : root.internal().collectChildrenDeep()) {
            if (child.getClass()==clazz && builderName.equals(child.internal().getTreeBuilderName())){
                ((M)child).utility().mock((f)->mock);
            }
        }
    }



}
