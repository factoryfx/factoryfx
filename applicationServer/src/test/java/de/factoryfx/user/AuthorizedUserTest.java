package de.factoryfx.user;

import java.util.Locale;

import org.junit.Test;

public class AuthorizedUserTest {

    @Test
    public void test_permission(){
        AuthorizedUser user = new AuthorizedUser("us",Locale.ENGLISH,"TEST_PERMISSION");

        user.checkPermission(null);
        //nothing
        user.checkPermission(null);
        //nothing
    }

    @Test(expected = IllegalStateException.class)
    public void test_permission_missing(){
        AuthorizedUser user = new AuthorizedUser("us",Locale.ENGLISH,"TEST_PERMISSION");

        user.checkPermission("hgfdhghgdh");
    }

}