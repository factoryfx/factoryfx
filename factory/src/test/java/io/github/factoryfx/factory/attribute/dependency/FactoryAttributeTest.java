package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;


public class FactoryAttributeTest {

    @Test
    public void test_null(){
        FactoryAttribute<ExampleFactoryA,ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryAttribute<ExampleFactoryA,ExampleLiveObjectA, ExampleFactoryA>();

        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(1, validationErrors.size());
        }

        {
            attribute.set(new ExampleFactoryA());
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_internal_require_true(){
        FactoryAttribute<ExampleFactoryA,ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryAttribute<>();
        Assertions.assertTrue(attribute.internal_required());
    }

    @Test
    public void test_internal_require_false(){
        FactoryAttribute<ExampleFactoryA,ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryAttribute<ExampleFactoryA,ExampleLiveObjectA, ExampleFactoryA>().nullable();
        Assertions.assertFalse(attribute.internal_required());
    }



    @Test
    public void test_nullable(){
        FactoryAttribute<ExampleFactoryA,ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryAttribute<ExampleFactoryA,ExampleLiveObjectA, ExampleFactoryA>().nullable();


        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }

        {
            attribute.set(new ExampleFactoryA());
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }
    }

    public static class CreateExampleFactory extends SimpleFactoryBase<Void,ExampleFactoryA> {
        @SuppressWarnings("unchecked")
        FactoryAttribute<ExampleFactoryA, ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryAttribute<ExampleFactoryA, ExampleLiveObjectA, ExampleFactoryA>().nullable();
        @Override
        public Void createImpl() {
            return null;
        }
    }

    @Test
    public void test_create_json(){
        CreateExampleFactory factory = new CreateExampleFactory();
        factory.attribute.set(new ExampleFactoryA());
        ObjectMapperBuilder.build().copy(factory);
    }

    @Test
    public void test_json(){
        ExampleFactoryA factory = new ExampleFactoryA();
        ObjectMapperBuilder.build().copy(factory);
    }

    public static class MockExampleFactoryRoot extends SimpleFactoryBase<Void,MockExampleFactoryRoot> {
        public final FactoryAttribute<MockExampleFactoryRoot, LiveDummy, ExampleFactory> attribute = new FactoryAttribute<>();
        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class ExampleFactory extends SimpleFactoryBase<LiveDummy,MockExampleFactoryRoot> {

        @Override
        public LiveDummy createImpl() {
            return new LiveDummy();
        }
    }

    public static class LiveDummy  {
        public void doX(){

        }
    }

    public static class MockExampleFactoryMock extends ExampleFactory {

        @Override
        public LiveDummy createImpl() {
            return Mockito.mock(LiveDummy.class);
        }
    }

    @Test
    public void test_mock(){
        MockExampleFactoryRoot root = new MockExampleFactoryRoot();
        root.attribute.set(new MockExampleFactoryMock());
    }

}