package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.Assert;
import org.junit.Test;

public class DataUpdateTest {

    @Test
    public void test_json(){
        DataUpdate<ExampleDataA> dataUpdate = new DataUpdate<>(new ExampleDataA(),"1","2","3");
        DataUpdate copy= ObjectMapperBuilder.build().copy(dataUpdate);
        Assert.assertEquals(dataUpdate.user,"1");
        Assert.assertEquals(dataUpdate.comment,"2");
        Assert.assertEquals(dataUpdate.baseVersionId,"3");
        Assert.assertNotNull(dataUpdate.root);
    }

}