package de.factoryfx.server.user.persistent;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class UserFactoryTest {

    @Test
    public void test_rename_type(){ //typo in permissions
        String old="{\n" +
                "  \"@class\" : \"de.factoryfx.server.user.persistent.UserFactory\",\n" +
                "  \"id\" : \"02136b53-88dc-e189-d878-0b9e86944076\",\n" +
                "  \"name\" : { },\n" +
                "  \"password\" : { },\n" +
                "  \"locale\" : { },\n" +
                "  \"permissons\" : [ \"ABC\" ]\n" +
                "}";


        UserFactory userFactory = ObjectMapperBuilder.build().readValue(old, UserFactory.class);
        Assert.assertEquals("ABC",userFactory.permissions.get().get(0));


    }

}