package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataUpdateTest {

    @Test
    public void test_json(){
        DataUpdate<ExampleDataA> dataUpdate = new DataUpdate<>(new ExampleDataA(),"1","2","3");
        DataUpdate copy= ObjectMapperBuilder.build().copy(dataUpdate);
        Assertions.assertEquals(dataUpdate.user,"1");
        Assertions.assertEquals(dataUpdate.comment,"2");
        Assertions.assertEquals(dataUpdate.baseVersionId,"3");
        Assertions.assertNotNull(dataUpdate.root);
    }

}