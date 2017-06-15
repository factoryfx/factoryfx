package de.factoryfx.factory.atrribute;

import java.util.Arrays;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Test;

public class FactoryViewListReferenceAttributeTest {
    @Test
    public void test_jon(){
        FactoryViewListReferenceAttribute<ExampleFactoryA,ExampleLiveObjectA,ExampleFactoryA> attribute = new FactoryViewListReferenceAttribute<>(o -> {
            return Arrays.asList(new ExampleFactoryA());
        });

        ObjectMapperBuilder.build().copy(attribute);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(attribute));
    }
}