package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.parametrized.ParametrizedObjectCreatorAttribute;
import io.github.factoryfx.factory.parametrized.ParametrizedObjectCreatorFactory;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Function;

public class DefaultCreatorTest {

    public static class DummyCreatorFactory extends ParametrizedObjectCreatorFactory<Void,Void,ParametrizedTestFactory> {
        @Override
        protected Function<Void, Void> getCreator() {
            return p -> null;
        }
    }


    public static class ParametrizedTestFactory extends SimpleFactoryBase<Void, ParametrizedTestFactory> {
        public final ParametrizedObjectCreatorAttribute<ParametrizedTestFactory,Void,Void,DummyCreatorFactory> dummyCreator =new ParametrizedObjectCreatorAttribute<>();

        @Override
        public Void createImpl() {
            return null;
        }
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

    public static class ExampleFactoryANotNullable extends SimpleFactoryBase<ExampleLiveObjectA, ExampleFactoryANotNullable> {
        public final FactoryReferenceAttribute<ExampleFactoryANotNullable,ExampleLiveObjectA,ExampleFactoryANotNullable> referenceAttribute = new FactoryReferenceAttribute<>();

        @Override
        public ExampleLiveObjectA createImpl() {
            return new ExampleLiveObjectA(null, null);
        }


    }


    @Test
    public void test_missing_factory(){
        Assertions.assertThrows(IllegalStateException.class, () -> {
            DefaultCreator<ExampleFactoryANotNullable, ExampleFactoryANotNullable> defaultCreator = new DefaultCreator<>(ExampleFactoryANotNullable.class);
            defaultCreator.apply(new FactoryContext<>());
        });
    }

    public static class ExampleFactoryNullableRef extends SimpleFactoryBase<ExampleLiveObjectA, ExampleFactoryNullableRef> {
        public final FactoryReferenceAttribute<ExampleFactoryNullableRef,ExampleLiveObjectA, ExampleFactoryNullableRef> referenceAttribute = new FactoryReferenceAttribute<ExampleFactoryNullableRef,ExampleLiveObjectA, ExampleFactoryNullableRef>().nullable();

        @Override
        public ExampleLiveObjectA createImpl() {
            return new ExampleLiveObjectA(null, null);
        }


    }

    @Test
    public void test_missing_factory_nullable(){
        DefaultCreator<ExampleFactoryNullableRef,ExampleFactoryNullableRef> defaultCreator = new DefaultCreator<>(ExampleFactoryNullableRef.class);
        defaultCreator.apply(new FactoryContext<>());
    }


}