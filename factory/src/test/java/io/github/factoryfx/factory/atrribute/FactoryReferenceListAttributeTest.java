package io.github.factoryfx.factory.atrribute;

import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FactoryReferenceListAttributeTest {

    @Test
    public void test_filtered_instance(){
        FactoryReferenceListAttribute<ExampleLiveObjectA,ExampleFactoryA> attribute = new FactoryReferenceListAttribute<>(ExampleFactoryA.class);
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