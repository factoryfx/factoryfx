package de.factoryfx.server.user.persistent;

import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.server.user.persistent.PersistentUserManagementFactory;
import de.factoryfx.server.user.persistent.UserFactory;
import org.junit.Assert;
import org.junit.Test;

public class PersistentUserManagementFactoryTest {

    @Test
    public void test_unique_username_happy_case(){
        PersistentUserManagementFactory<Void,ExampleFactoryA> persistentUserManagementFactory = new PersistentUserManagementFactory<>();
        {
            UserFactory<Void,ExampleFactoryA> userFactory = new UserFactory<>();
            userFactory.name.set("1");
            persistentUserManagementFactory.users.add(userFactory);
        }
        {
            UserFactory<Void,ExampleFactoryA> userFactory = new UserFactory<>();
            userFactory.name.set("2");
            persistentUserManagementFactory.users.add(userFactory);
        }
        Assert.assertEquals(0,persistentUserManagementFactory.internal().validateFlat().size());
    }

    @Test
    public void test_unique_username_error(){
        PersistentUserManagementFactory<Void,ExampleFactoryA> persistentUserManagementFactory = new PersistentUserManagementFactory<>();
        {
            UserFactory<Void,ExampleFactoryA> userFactory = new UserFactory<>();
            userFactory.name.set("1");
            persistentUserManagementFactory.users.add(userFactory);
        }
        {
            UserFactory<Void,ExampleFactoryA> userFactory = new UserFactory<>();
            userFactory.name.set("1");
            persistentUserManagementFactory.users.add(userFactory);
        }
        Assert.assertEquals(1,persistentUserManagementFactory.internal().validateFlat().size());
    }


}