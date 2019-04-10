package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FactoryListAttributeTest {

    @Test
    public void test_filtered_instance(){
        FactoryListAttribute<ExampleFactoryA,ExampleLiveObjectA,ExampleFactoryA> attribute = new FactoryListAttribute<>();
        {
            ExampleFactoryA data = new ExampleFactoryA();
            data.stringAttribute.set("1");
            attribute.add(data);
        }
        {
            ExampleFactoryA data = new ExampleFactoryA();
            data.stringAttribute.set("2");
            attribute.add(data);
        }
        {
            ExampleFactoryA data = new ExampleFactoryA();
            data.stringAttribute.set("3");
            attribute.add(data);
        }

        ExampleLiveObjectA instance = attribute.instances(exampleFactoryA -> exampleFactoryA.stringAttribute.get().equals("1"));
        Assertions.assertNotNull(instance);
    }

}