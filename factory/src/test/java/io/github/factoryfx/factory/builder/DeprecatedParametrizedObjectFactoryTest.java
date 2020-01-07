package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DeprecatedParametrizedObjectFactoryTest {
    public static class ParametrizedObjectCreator<P,L>{

        private final Function<P,L> creator;

        public ParametrizedObjectCreator(Function<P, L> creator) {
            this.creator = creator;
        }

        public L create(P transientParameter){
            return creator.apply(transientParameter);
        }
    }

    public static class ParametrizedObjectCreatorAttribute<R extends FactoryBase<?,R>,P, L, F extends ParametrizedObjectCreatorFactory<P,L,R>> extends FactoryBaseAttribute<ParametrizedObjectCreator<P,L>,F,ParametrizedObjectCreatorAttribute<R,P, L, F>> {

        public ParametrizedObjectCreatorAttribute() {
            super();
        }

        public L create(P p){
            ParametrizedObjectCreator<P,L> instance = this.instance();
            return instance.create(p);
        }

    }

    public static abstract class ParametrizedObjectCreatorFactory<P,L,R extends FactoryBase<?,R>> extends FactoryBase<ParametrizedObjectCreator<P,L>,R> {


        public ParametrizedObjectCreatorFactory(){
            this.configLifeCycle().setCreator(() -> new ParametrizedObjectCreator<>(getCreator()));
        }

        protected abstract Function<P,L> getCreator();

    }

    public static class DummyCreatorFactory extends ParametrizedObjectCreatorFactory<Void,Void,ParametrizedTestFactory> {
        @Override
        protected Function<Void, Void> getCreator() {
            return p -> null;
        }
    }


    public static class ParametrizedTestFactory extends SimpleFactoryBase<Void, ParametrizedTestFactory> {
        public final ParametrizedObjectCreatorAttribute<ParametrizedTestFactory,Void,Void,DummyCreatorFactory> dummyCreator =new ParametrizedObjectCreatorAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @Test
    public void test_metatda(){
        ParametrizedTestFactory parametrizedTestFactory = new ParametrizedTestFactory();
        List<Class<?>> referenceClass=new ArrayList<>();
        parametrizedTestFactory.internal().visitAttributesFlat((attributeMetadata, attribute) -> referenceClass.add(attributeMetadata.referenceClass));
        Assertions.assertEquals(1, referenceClass.size());
        Assertions.assertEquals(DummyCreatorFactory.class, referenceClass.get(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_ParametrizedObjectCreatorAttribute(){
        DefaultCreator<ParametrizedTestFactory,ParametrizedTestFactory> defaultCreator = new DefaultCreator<>(ParametrizedTestFactory.class);

        FactoryContext<ParametrizedTestFactory> mock = Mockito.mock(FactoryContext.class);
        DummyCreatorFactory value = new DummyCreatorFactory();
        Mockito.when(mock.getUnchecked(Mockito.any(Class.class))).thenReturn(value);

        ParametrizedTestFactory factory= defaultCreator.apply(mock);

        Assertions.assertNotNull(factory.dummyCreator.get());
        DummyCreatorFactory actual = factory.dummyCreator.get();
        Assertions.assertEquals(DummyCreatorFactory.class, actual.getClass());
    }
}
