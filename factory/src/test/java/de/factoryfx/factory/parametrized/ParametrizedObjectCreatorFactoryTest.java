package de.factoryfx.factory.parametrized;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
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

    public static class ShortLivedLiveObjectCreatorFactory extends ParametrizedObjectCreatorFactory<ShortLivedParameter,ShortLivedLiveObject,Void,ShortLivedLiveObjectCreatorFactory> {
        public final FactoryReferenceAttribute<ExampleLiveObjectA,ExampleFactoryA> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryA.class);

        @Override
        protected Function<ShortLivedParameter, ShortLivedLiveObject> getCreator() {
            return shortLivedParameter -> new ShortLivedLiveObject(referenceAttribute.instance(), shortLivedParameter.test);
        }
    }

    public static class TestShortLivedUserFactory extends SimpleFactoryBase<TestShortLivedUser,Void,TestShortLivedUserFactory> {
        public final ParametrizedObjectCreatorAttribute<ShortLivedParameter,ShortLivedLiveObject,ShortLivedLiveObjectCreatorFactory> builder = new ParametrizedObjectCreatorAttribute<>(ShortLivedLiveObjectCreatorFactory.class);

        @Override
        public TestShortLivedUser createImpl() {
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
        creator.referenceAttribute.set(new ExampleFactoryA());

        TestShortLivedUserFactory testShortLivedUserFactory = new TestShortLivedUserFactory();
        testShortLivedUserFactory.builder.set(creator);
    }

    @Test
    public void test_json(){
        ShortLivedLiveObjectCreatorFactory creator = new ShortLivedLiveObjectCreatorFactory();
        creator.referenceAttribute.set(new ExampleFactoryA());

        TestShortLivedUserFactory testShortLivedUserFactory = new TestShortLivedUserFactory();
        testShortLivedUserFactory.builder.set(creator);

        ObjectMapperBuilder.build().copy(testShortLivedUserFactory);
    }
}