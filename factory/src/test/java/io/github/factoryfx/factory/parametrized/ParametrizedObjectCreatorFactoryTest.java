package io.github.factoryfx.factory.parametrized;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

public class ParametrizedObjectCreatorFactoryTest {
    public static class ShortLivedParameter{
        public String test;

        public ShortLivedParameter(String test) {
            this.test = test;
        }
    }

    public static class ShortLivedLiveObject{
        private final ExampleLiveObjectA exampleLiveObjectA;
        private final String test;

        private ShortLivedLiveObject(ExampleLiveObjectA exampleLiveObjectA, String test) {
            this.exampleLiveObjectA = exampleLiveObjectA;
            this.test = test;
        }
    }

    public static class ShortLivedLiveObjectCreatorFactory extends ParametrizedObjectCreatorFactory<ShortLivedParameter,ShortLivedLiveObject,ShortLivedLiveObjectCreatorFactory> {
        public final FactoryAttribute<ExampleLiveObjectA,ShortLivedLiveObjectCreatorExampleFactoryA> referenceAttribute = new FactoryAttribute<>();

        @Override
        protected Function<ShortLivedParameter, ShortLivedLiveObject> getCreator() {
            return shortLivedParameter -> new ShortLivedLiveObject(referenceAttribute.instance(), shortLivedParameter.test);
        }
    }

    public static class ShortLivedLiveObjectCreatorExampleFactoryA extends FactoryBase<ExampleLiveObjectA,ShortLivedLiveObjectCreatorFactory>{

    }

    public static class TestShortLivedUserFactory extends SimpleFactoryBase<TestShortLivedUser,ShortLivedLiveObjectCreatorFactory> {
        public final ParametrizedObjectCreatorAttribute<ShortLivedLiveObjectCreatorFactory,ShortLivedParameter,ShortLivedLiveObject,ShortLivedLiveObjectCreatorFactory> builder = new ParametrizedObjectCreatorAttribute<>();

        @Override
        protected TestShortLivedUser createImpl() {
            return new TestShortLivedUser(builder.instance());
        }
    }

    public static class TestShortLivedUser{
        private final ParametrizedObjectCreator<ShortLivedParameter,ShortLivedLiveObject> creator;

        private TestShortLivedUser(ParametrizedObjectCreator<ShortLivedParameter, ShortLivedLiveObject> creator) {
            this.creator = creator;

            creator.create(new ShortLivedParameter("blub"));
        }
    }


    @Test
    public void test(){
        ShortLivedLiveObjectCreatorFactory creator = new ShortLivedLiveObjectCreatorFactory();
        creator.referenceAttribute.set(new ShortLivedLiveObjectCreatorExampleFactoryA());

        TestShortLivedUserFactory testShortLivedUserFactory = new TestShortLivedUserFactory();
        testShortLivedUserFactory.builder.set(creator);
    }

    @Test
    public void test_json(){
        ShortLivedLiveObjectCreatorFactory creator = new ShortLivedLiveObjectCreatorFactory();
        creator.referenceAttribute.set(new ShortLivedLiveObjectCreatorExampleFactoryA());

        TestShortLivedUserFactory testShortLivedUserFactory = new TestShortLivedUserFactory();
        testShortLivedUserFactory.builder.set(creator);

        ObjectMapperBuilder.build().copy(testShortLivedUserFactory);
    }
}