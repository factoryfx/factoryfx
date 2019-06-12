package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.SimpleFactoryBase;
import org.junit.jupiter.api.Test;

public class FactoryViewReferenceAttributeTest {

    public static class ExampleViewFactory extends SimpleFactoryBase<Void,ExampleViewFactory> {
        public final FactoryViewAttribute<ExampleViewFactory,Void,ExampleViewFactory> attribute = new FactoryViewAttribute<>(exampleViewFactory -> null);

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @Test
    public void test_json(){
        ObjectMapperBuilder.build().copy(new ExampleViewFactory());
    }

}