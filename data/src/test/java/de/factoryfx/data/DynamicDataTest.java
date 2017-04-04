package de.factoryfx.data;

import java.util.List;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.attribute.types.StringListAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class DynamicDataTest {


    @Test
    public void test_json(){
        DynamicData dynamicData = new DynamicData();
        final StringAttribute stringAttribute = new StringAttribute(new AttributeMetadata());
        stringAttribute.set("fdg");
        dynamicData.addAttribute(stringAttribute);
        dynamicData.addAttribute(new IntegerAttribute(new AttributeMetadata()));

        DynamicData copy = ObjectMapperBuilder.build().copy(dynamicData);
        Assert.assertEquals(2, copy.getAttributes().size());
    }

    @Test
    public void test_json_list(){
        DynamicData dynamicData = new DynamicData();
        final StringListAttribute stringListAttribute = new StringListAttribute(new AttributeMetadata());
        stringListAttribute.add("1");
        stringListAttribute.add("2");
        dynamicData.addAttribute(stringListAttribute);

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(dynamicData));

        DynamicData copy = ObjectMapperBuilder.build().copy(dynamicData);
        Assert.assertEquals(1, copy.getAttributes().size());
        Assert.assertEquals("1", ((List<String>)copy.getAttributes().get(0).attribute.get()).get(0));
    }

    @Ignore  //for now only value attributes are supported
    @Test
    public void test_json_ref(){
        DynamicData dynamicData = new DynamicData();
        final ReferenceAttribute<ExampleFactoryA> referenceAttribute = new ReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata());
        final ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        referenceAttribute.set(exampleFactoryA);

//        dynamicData.addAttribute(referenceAttribute,"test");

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(dynamicData));

        DynamicData copy = ObjectMapperBuilder.build().copy(dynamicData);
        Assert.assertEquals(1, copy.getAttributes().size());
        Assert.assertEquals(exampleFactoryA, ((ExampleFactoryA)copy.getAttributes().get(0).attribute.get()));
    }

    @Test
    public void test_merge(){
        DynamicData currentModel = new DynamicData();
        {
            final StringAttribute stringAttribute = new StringAttribute(new AttributeMetadata());
            stringAttribute.set("1");
            currentModel.addAttribute(stringAttribute);
        }
        DynamicData originalModel = currentModel.internal().copy();
        {
            //same
        }
        DynamicData newModel = currentModel.internal().copy();
        {
            ((StringAttribute)newModel.getAttributes().get(0).attribute).set("2");
        }
        Assert.assertEquals("1",currentModel.getAttributes().get(0).attribute.get());
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("2",currentModel.getAttributes().get(0).attribute.get());

    }
}