package de.factoryfx.factory.builder;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.parametrized.ParametrizedObjectCreatorAttribute;
import de.factoryfx.factory.parametrized.ParametrizedObjectCreatorFactory;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectB;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.Function;

public class DefaultCreatorTest {

    public static class DummyCreatorFactory extends ParametrizedObjectCreatorFactory<Void,Void,Void,DummyCreatorFactory> {
        @Override
        protected Function<Void, Void> getCreator() {
            return p -> null;
        }
    }


    public static class ParametrizedTestFactory extends SimpleFactoryBase<Void,Void, ParametrizedTestFactory> {
        public final ParametrizedObjectCreatorAttribute<Void,Void,DummyCreatorFactory> dummyCreator =new ParametrizedObjectCreatorAttribute<>(DummyCreatorFactory.class);

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

        Assert.assertNotNull(factory.dummyCreator.get());
        DummyCreatorFactory actual = factory.dummyCreator.get();
        Assert.assertEquals(DummyCreatorFactory.class, actual.getClass());
    }


    @Test(expected = IllegalStateException.class)
    public void test_missing_factory(){
        DefaultCreator<ExampleFactoryA,ExampleFactoryA> defaultCreator = new DefaultCreator<>(ExampleFactoryA.class);
        defaultCreator.apply(new FactoryContext<>());
    }

    public static class ExampleFactoryNullableRef extends SimpleFactoryBase<ExampleLiveObjectA,Void, ExampleFactoryNullableRef> {
        public final FactoryReferenceAttribute<ExampleLiveObjectB, ExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryB.class).nullable();

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