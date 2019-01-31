package de.factoryfx.data.attribute;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.merge.testdata.ExampleDataB;
import org.junit.Assert;
import org.junit.Test;

public class DataDictionaryAttributeTest {

    @Test
    public void test_json(){
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.referenceAttribute.set(new ExampleDataB());

        ExampleDataA copy = ObjectMapperBuilder.build().copy(exampleDataA);
        Assert.assertNotNull(copy);
    }

    @Test
    public void test_default_value_json_inside_data(){
        ExampleReferenceData value = new ExampleReferenceData();
        value.attribute.set(new ExampleDataA());
        ExampleReferenceData copy = ObjectMapperBuilder.build().copy(value);
        Assert.assertNotNull(copy.attribute.get());
    }

    public static class ExampleReferenceData{
        public final DataReferenceAttribute<ExampleDataA> attribute = new DataReferenceAttribute<>(ExampleDataA.class);

    }
}
