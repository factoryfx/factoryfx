package de.factoryfx.data.attribute;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.Assert;
import org.junit.Test;

public class DataDictionaryListAttributeTest {

    @Test
    public void test_json(){
        DataReferenceListAttribute<ExampleDataA> attribute = new DataReferenceListAttribute<>();
        attribute.add(new ExampleDataA());
        DataReferenceListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertTrue(copy.get().get(0) instanceof  ExampleDataA);
    }


}