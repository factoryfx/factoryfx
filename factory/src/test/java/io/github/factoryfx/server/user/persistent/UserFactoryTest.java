package io.github.factoryfx.server.user.persistent;

import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserFactoryTest {

    @Test
    public void test_rename_type(){ //typo in permissions
        String old="{\n" +
                "  \"@class\" : \"io.github.factoryfx.server.user.persistent.UserFactory\",\n" +
                "  \"id\" : \"02136b53-88dc-e189-d878-0b9e86944076\",\n" +
                "  \"name\" : { },\n" +
                "  \"password\" : { },\n" +
                "  \"locale\" : { },\n" +
                "  \"permissons\" : [ \"ABC\" ]\n" +
                "}";


        UserFactory userFactory = ObjectMapperBuilder.build().readValue(old, UserFactory.class);
        Assertions.assertEquals("ABC",userFactory.permissions.get().get(0));


    }

}