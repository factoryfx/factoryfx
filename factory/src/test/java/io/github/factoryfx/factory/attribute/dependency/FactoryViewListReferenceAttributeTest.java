package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.SimpleFactoryBase;
import org.junit.jupiter.api.Test;

public class FactoryViewListReferenceAttributeTest {
    public static class ExampleViewListFactory extends SimpleFactoryBase<Void, ExampleViewListFactory> {
        public final FactoryViewListAttribute<ExampleViewListFactory,Void, ExampleViewListFactory> attribute = new FactoryViewListAttribute<>(exampleViewFactory -> null);

        @Override
        public Void createImpl() {
            return null;
        }
    }


    @Test
    public void test_jon(){
        ObjectMapperBuilder.build().copy(new ExampleViewListFactory());
    }
}