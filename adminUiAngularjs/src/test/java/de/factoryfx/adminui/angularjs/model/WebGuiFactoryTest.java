package de.factoryfx.adminui.angularjs.model;

import de.factoryfx.adminui.angularjs.integration.example.ExampleFactoryA;
import de.factoryfx.adminui.angularjs.integration.example.ExampleFactoryB;
import de.factoryfx.factory.jackson.ObjectMapperBuilder;
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

        System.out.println(new ObjectMapperBuilder().build().writeValueAsString(new WebGuiFactory(exampleFactoryA,exampleFactoryA)));

        //test json serialisation
        new ObjectMapperBuilder().build().copy(new WebGuiFactory(exampleFactoryA,exampleFactoryA));

    }

}