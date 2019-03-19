package io.github.factoryfx.server.user;

import java.util.Locale;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthorizedUserTest {

    @Test
    public void test_permission(){
        AuthorizedUser user = new AuthorizedUser("us",Locale.ENGLISH,"TEST_PERMISSION");

        user.checkPermission(null);
        //nothing
        user.checkPermission(null);
        //nothing
    }

    @Test
    public void test_permission_missing(){
        Assertions.assertThrows(IllegalStateException.class, () -> {
            AuthorizedUser user = new AuthorizedUser("us", Locale.ENGLISH, "TEST_PERMISSION");

            user.checkPermission("hgfdhghgdh");
        });
    }

}