package de.factoryfx.user.persistent;

import org.junit.Assert;
import org.junit.Test;
import org.omg.PortableInterceptor.USER_EXCEPTION;

import static org.junit.Assert.*;

public class PersistentUserManagementFactoryTest {

    @Test
    public void test_unique_username_happy_case(){
        PersistentUserManagementFactory<Void> persistentUserManagementFactory = new PersistentUserManagementFactory<>();
        {
            UserFactory<Void> userFactory = new UserFactory<>();
            userFactory.name.set("1");
            persistentUserManagementFactory.users.add(userFactory);
        }
        {
            UserFactory<Void> userFactory = new UserFactory<>();
            userFactory.name.set("2");
            persistentUserManagementFactory.users.add(userFactory);
        }
        Assert.assertEquals(0,persistentUserManagementFactory.internal().validateFlat().size());
    }

    @Test
    public void test_unique_username_error(){
        PersistentUserManagementFactory<Void> persistentUserManagementFactory = new PersistentUserManagementFactory<>();
        {
            UserFactory<Void> userFactory = new UserFactory<>();
            userFactory.name.set("1");
            persistentUserManagementFactory.users.add(userFactory);
        }
        {
            UserFactory<Void> userFactory = new UserFactory<>();
            userFactory.name.set("1");
            persistentUserManagementFactory.users.add(userFactory);
        }
        Assert.assertEquals(1,persistentUserManagementFactory.internal().validateFlat().size());
    }


}