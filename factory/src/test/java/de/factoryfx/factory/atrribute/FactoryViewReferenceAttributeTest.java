package de.factoryfx.factory.atrribute;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.SimpleFactoryBase;
import org.junit.jupiter.api.Test;

public class FactoryViewReferenceAttributeTest {

    public static class ExampleViewFactory extends SimpleFactoryBase<Void,Void,ExampleViewFactory>{
        public final FactoryViewReferenceAttribute<ExampleViewFactory,Void,ExampleViewFactory> attribute = new FactoryViewReferenceAttribute<>(exampleViewFactory -> null);

        @Override
        public Void createImpl() {
            return null;
        }
    }

    @Test
    public void test_jon(){
        ObjectMapperBuilder.build().copy(new ExampleViewFactory());
    }

}