package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class FactoryListAttributeTest {

    @Test
    public void test_filtered_instance(){
        FactoryListAttribute<ExampleLiveObjectA,ExampleFactoryA> attribute = new FactoryListAttribute<>();
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

    @Test
    public void test_batchModify(){
        FactoryListAttribute<ExampleLiveObjectA,ExampleFactoryA> attribute = new FactoryListAttribute<>();

        ArrayList<String> calls = new ArrayList<>();
        attribute.internal_addListener((attribute1, value) -> calls.add(""));

        attribute.batchModify(list -> {
            list.add(new ExampleFactoryA());
            list.add(new ExampleFactoryA());
            list.add(new ExampleFactoryA());
        });

        Assertions.assertEquals(1,calls.size());

    }


}