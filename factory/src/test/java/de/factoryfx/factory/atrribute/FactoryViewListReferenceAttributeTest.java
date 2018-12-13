package de.factoryfx.factory.atrribute;

import java.util.Arrays;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Test;

public class FactoryViewListReferenceAttributeTest {
    public static class ExampleViewListFactory extends SimpleFactoryBase<Void,Void, ExampleViewListFactory> {
        public final FactoryViewListReferenceAttribute<ExampleViewListFactory,Void, ExampleViewListFactory> attribute = new FactoryViewListReferenceAttribute<>(exampleViewFactory -> null);

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