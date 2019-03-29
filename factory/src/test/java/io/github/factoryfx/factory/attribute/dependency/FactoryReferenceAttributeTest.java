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


public class FactoryReferenceAttributeTest {

    @Test
    public void test_null(){
        FactoryReferenceAttribute<ExampleFactoryA,ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryReferenceAttribute<ExampleFactoryA,ExampleLiveObjectA, ExampleFactoryA>();

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
        FactoryReferenceAttribute<ExampleFactoryA,ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryReferenceAttribute<>();
        Assertions.assertTrue(attribute.internal_required());
    }

    @Test
    public void test_internal_require_false(){
        FactoryReferenceAttribute<ExampleFactoryA,ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryReferenceAttribute<ExampleFactoryA,ExampleLiveObjectA, ExampleFactoryA>().nullable();
        Assertions.assertFalse(attribute.internal_required());
    }



    @Test
    public void test_nullable(){
        FactoryReferenceAttribute<ExampleFactoryA,ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryReferenceAttribute<ExampleFactoryA,ExampleLiveObjectA, ExampleFactoryA>().nullable();


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
        FactoryReferenceAttribute<ExampleFactoryA, ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryReferenceAttribute<ExampleFactoryA, ExampleLiveObjectA, ExampleFactoryA>().nullable();
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
        public final FactoryReferenceAttribute<MockExampleFactoryRoot, LiveDummy, ExampleFactory> attribute = new FactoryReferenceAttribute<>();
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