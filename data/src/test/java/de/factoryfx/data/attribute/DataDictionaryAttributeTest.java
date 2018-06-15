package de.factoryfx.data.attribute;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.Assert;
import org.junit.Test;

public class DataDictionaryAttributeTest {

    @Test
    public void test_default_value_json(){
        DataReferenceAttribute<ExampleDataA> dataReferenceAttribute = new DataReferenceAttribute<>(ExampleDataA.class).defaultValue(new ExampleDataA());
        DataReferenceAttribute<ExampleDataA> copy = ObjectMapperBuilder.build().copy(dataReferenceAttribute);
        Assert.assertNotNull(copy.get());
    }

    @Test
    public void test_default_value_json_inside_data(){
        ExampleReferenceData copy = ObjectMapperBuilder.build().copy(new ExampleReferenceData());
        Assert.assertNotNull(copy.attribute.get());
    }

    public static class ExampleReferenceData{
        public final DataReferenceAttribute<ExampleDataA> attribute = new DataReferenceAttribute<>(ExampleDataA.class).defaultValue(new ExampleDataA());

    }
}
