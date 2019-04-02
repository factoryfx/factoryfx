package io.github.factoryfx.factory;

import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectB;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTreeCreatorTest {

    @Test
    public void test(){
        ExampleFactoryA root = new ExampleFactoryA();
        root.referenceAttribute.set(new ExampleFactoryB());
        SubTreeCreator<ExampleFactoryA,ExampleLiveObjectB,ExampleFactoryB> subTreeCreator = new SubTreeCreator<>(root,(r)-> r.referenceAttribute.get() );
        ExampleLiveObjectB exampleLiveObjectB1 = subTreeCreator.create();
        ExampleLiveObjectB exampleLiveObjectB2 = subTreeCreator.create();
        assertNotEquals(exampleLiveObjectB1,exampleLiveObjectB2);
    }

}