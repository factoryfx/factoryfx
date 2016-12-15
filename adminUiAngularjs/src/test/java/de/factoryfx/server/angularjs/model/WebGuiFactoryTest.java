package de.factoryfx.server.angularjs.model;

import de.factoryfx.server.angularjs.integration.example.ExampleFactoryA;
import de.factoryfx.server.angularjs.integration.example.ExampleFactoryB;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class WebGuiFactoryTest {

    @Test
    public void test_json(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("balblub");
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.stringAttribute.set("BBBBBBBBBBBBBBBB");
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.referenceListAttribute.add(exampleFactoryA.referenceAttribute.get());

        exampleFactoryA = exampleFactoryA.internal().prepareUsableCopy();

        //test json serialisation
        new ObjectMapperBuilder().build().copy(new WebGuiFactory(exampleFactoryA,exampleFactoryA));

    }

}