package de.factoryfx.factory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.jackson.SimpleObjectMapper;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleFactoryC;
import org.junit.Assert;
import org.junit.Test;

public class FactoryBaseTest {

    @Test(expected = IllegalStateException.class)
    public void create_loop_test(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.referenceAttribute.set(exampleFactoryA);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.loopDetector();
    }

    @Test
    public void test_collect_Live_Objects(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.create();

        HashMap<String, LiveObject> liveObjects = new HashMap<>();
        exampleFactoryA.collectLiveObjects(liveObjects);

        Assert.assertEquals(3,liveObjects.entrySet().size());
    }


    @Test
    public void test_visitAttributes(){
        ExampleFactoryA testModel = new ExampleFactoryA();
        testModel.stringAttribute.set("xxxx");
        testModel.referenceAttribute.set(new ExampleFactoryB());

        ArrayList<String> calls = new ArrayList<>();
        testModel.visitAttributesFlat(attribute -> calls.add(attribute.get().toString()));
        Assert.assertEquals(3,calls.size());
        Assert.assertEquals("xxxx",calls.get(0));
    }

    @Test
    public void test_reconstructMetadataDeepRoot(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.referenceListAttribute.add(new ExampleFactoryB());

        SimpleObjectMapper mapper = ObjectMapperBuilder.build();
        String string = mapper.writeValueAsString(exampleFactoryA);
        ExampleFactoryA readed = null;
        try {
            readed = mapper.getObjectMapper().readValue(string,ExampleFactoryA.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(null,readed.stringAttribute);
        Assert.assertEquals(null,readed.referenceAttribute.metadata.labelText);
        Assert.assertEquals(null,readed.referenceListAttribute.metadata.labelText);
        Assert.assertEquals(null,readed.referenceAttribute.get().stringAttribute);
        Assert.assertEquals(null,readed.referenceListAttribute.get(0).stringAttribute);

        readed.reconstructMetadataDeepRoot();

        Assert.assertEquals("ExampleA1",readed.stringAttribute.metadata.labelText);
        Assert.assertEquals("ExampleA2",readed.referenceAttribute.metadata.labelText);
        Assert.assertEquals("ExampleA3",readed.referenceListAttribute.metadata.labelText);
        Assert.assertEquals("ExampleB1",readed.referenceAttribute.get().stringAttribute.metadata.labelText);
        Assert.assertEquals("ExampleB1",readed.referenceListAttribute.get(0).stringAttribute.metadata.labelText);
    }

    @Test
    public void test_copyOneLevelDeep(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);

        ExampleFactoryB factoryB = new ExampleFactoryB();
        factoryB.referenceAttributeC.set(new ExampleFactoryC());
        exampleFactoryA.referenceListAttribute.add(factoryB);

        Assert.assertNotNull(exampleFactoryA.referenceAttribute.get());
        Assert.assertNotNull(exampleFactoryA.referenceAttribute.get().referenceAttributeC.get());

        ExampleFactoryA copy =  exampleFactoryA.copyOneLevelDeep();

        Assert.assertNotEquals(copy,exampleFactoryA);
        Assert.assertNotNull(copy.referenceAttribute.get());
        Assert.assertNull(copy.referenceAttribute.get().referenceAttributeC.get());
        Assert.assertNull(copy.referenceListAttribute.get(0).referenceAttributeC.get());
    }

    @Test
    public void test_getPathTo(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        List<FactoryBase<?, ?>> pathTo = exampleFactoryA.getPathTo(exampleFactoryB);
        Assert.assertEquals(1,pathTo.size());
        Assert.assertEquals(exampleFactoryA.getId(),pathTo.get(0).getId());
    }
}