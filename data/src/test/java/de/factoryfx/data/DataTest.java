package de.factoryfx.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.jackson.SimpleObjectMapper;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import de.factoryfx.data.merge.testfactories.ExampleFactoryB;
import de.factoryfx.data.merge.testfactories.ExampleFactoryC;
import de.factoryfx.data.merge.testfactories.IdData;
import org.junit.Assert;
import org.junit.Test;

public class DataTest {

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
        Assert.assertEquals(null,readed.referenceAttribute.metadata);
        Assert.assertEquals(null,readed.referenceListAttribute.metadata);
        Assert.assertEquals(null,readed.referenceAttribute.get().stringAttribute);
        Assert.assertEquals(null,readed.referenceListAttribute.get(0).stringAttribute);

        readed.reconstructMetadataDeepRoot();

        Assert.assertEquals("ExampleA1",readed.stringAttribute.metadata.labelText.getPreferred(Locale.ENGLISH));
        Assert.assertEquals("ExampleA2",readed.referenceAttribute.metadata.labelText.getPreferred(Locale.ENGLISH));
        Assert.assertEquals("ExampleA3",readed.referenceListAttribute.metadata.labelText.getPreferred(Locale.ENGLISH));
        Assert.assertEquals("ExampleB1",readed.referenceAttribute.get().stringAttribute.metadata.labelText.getPreferred(Locale.ENGLISH));
        Assert.assertEquals("ExampleB1",readed.referenceListAttribute.get(0).stringAttribute.metadata.labelText.getPreferred(Locale.ENGLISH));
    }


    @Test
    public void test_getPathTo(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        List<Data> pathTo = exampleFactoryA.getPathTo(exampleFactoryB);
        Assert.assertEquals(1,pathTo.size());
        Assert.assertEquals(exampleFactoryA.getId(),pathTo.get(0).getId());
    }


    public static class ExampleObjectProperty extends IdData {
        public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("stringAttribute"));
        public final ObjectValueAttribute<String> objectValueAttribute= new ObjectValueAttribute<>(new AttributeMetadata().labelText("objectValueAttribute"));
    }


}