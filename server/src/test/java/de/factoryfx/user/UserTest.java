package de.factoryfx.user;

import java.util.Locale;

import org.junit.Test;

public class UserTest {

    @Test
    public void test_permission(){
        User user = new User("us","pw", Locale.ENGLISH,"TEST_PERMISSION");

        user.checkPermission(null);
        //nothing
        user.checkPermission(null);
        //nothing
    }

    @Test(expected = IllegalStateException.class)
    public void test_permission_missing(){
        User user = new User("us","pw", Locale.ENGLISH,"TEST_PERMISSION");

        user.checkPermission("hgfdhghgdh");
    }

}