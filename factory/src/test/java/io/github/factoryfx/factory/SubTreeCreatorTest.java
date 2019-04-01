package io.github.factoryfx.factory;

import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectB;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTreeCreatorTest {

    @Test
    public void test(){
        SubTreeCreator<ExampleFactoryA,ExampleLiveObjectB,ExampleFactoryB> subTreeCreator = new SubTreeCreator<>((r)-> r.referenceAttribute.get() );
        ExampleFactoryA root = new ExampleFactoryA();
        root.referenceAttribute.set(new ExampleFactoryB());
        ExampleLiveObjectB exampleLiveObjectB1 = subTreeCreator.create(root);
        ExampleLiveObjectB exampleLiveObjectB2 = subTreeCreator.create(root);
        assertNotEquals(exampleLiveObjectB1,exampleLiveObjectB2);
    }

}