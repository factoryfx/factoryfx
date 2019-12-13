package io.github.factoryfx.microservice.rest;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.microservice.common.UserAwareRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MicroserviceResourceTest {
    @Test
    public void test_json(){
        DataUpdate<ExampleFactoryA> dataUpdate = new DataUpdate<>(new ExampleFactoryA(),"1","2","3");
        DataUpdate copy= ObjectMapperBuilder.build().copy(dataUpdate);
        Assertions.assertEquals(dataUpdate.user,"1");
        Assertions.assertEquals(dataUpdate.comment,"2");
        Assertions.assertEquals(dataUpdate.baseVersionId,"3");
        Assertions.assertNotNull(dataUpdate.root);


        System.out.println(ObjectMapperBuilder.build().writeValueAsString((new UserAwareRequest<DataUpdate>("","",dataUpdate))));
    }
}