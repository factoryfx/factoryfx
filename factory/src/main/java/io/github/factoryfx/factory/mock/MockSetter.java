package io.github.factoryfx.factory.mock;

import io.github.factoryfx.factory.FactoryBase;

import java.util.function.Function;

/** utility for testing
 *
 * set mock for factory based on class or builderName
 * @param <R> root
 */
public class MockSetter<R extends FactoryBase<?,R>> {
    private final  FactoryBase<?,R> root;

    public MockSetter(FactoryBase<?,R> root){
        this.root=root;
    }

    public <L,M extends FactoryBase<L,R>> void setMock(Class<M> clazz, L mock){
        this.setMockCreator(clazz,(f)->mock);
    }


    public <L,M extends FactoryBase<L,R>> void setMock(Class<M> clazz, String builderName, L mock){
        this.setMockCreator(clazz,builderName,(f)->mock);
    }

    @SuppressWarnings("unchecked")
    public <L,M extends FactoryBase<L,R>> void setMockCreator(Class<M> clazz, Function<M,L> mockCreator){
        for (FactoryBase<?, R> child : root.internal().collectChildrenDeep()) {
            if (child.getClass()==clazz){
                ((M)child).utility().mock(mockCreator);
            }
        }

    }

    @SuppressWarnings("unchecked")
    public <L,M extends FactoryBase<L,R>> void setMockCreator(Class<M> clazz, String builderName, Function<M,L> mockCreator){
        for (FactoryBase<?, R> child : root.internal().collectChildrenDeep()) {
            if (child.getClass()==clazz && builderName.equals(child.internal().getTreeBuilderName())){
                ((M)child).utility().mock(mockCreator);
            }
        }
    }

}
