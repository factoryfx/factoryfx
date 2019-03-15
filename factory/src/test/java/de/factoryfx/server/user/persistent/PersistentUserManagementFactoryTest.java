package de.factoryfx.server.user.persistent;

import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.server.user.persistent.PersistentUserManagementFactory;
import de.factoryfx.server.user.persistent.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PersistentUserManagementFactoryTest {

    @Test
    public void test_unique_username_happy_case(){
        PersistentUserManagementFactory<ExampleFactoryA> persistentUserManagementFactory = new PersistentUserManagementFactory<>();
        {
            UserFactory<ExampleFactoryA> userFactory = new UserFactory<>();
            userFactory.name.set("1");
            persistentUserManagementFactory.users.add(userFactory);
        }
        {
            UserFactory<ExampleFactoryA> userFactory = new UserFactory<>();
            userFactory.name.set("2");
            persistentUserManagementFactory.users.add(userFactory);
        }
        Assertions.assertEquals(0,persistentUserManagementFactory.internal().validateFlat().size());
    }

    @Test
    public void test_unique_username_error(){
        PersistentUserManagementFactory<ExampleFactoryA> persistentUserManagementFactory = new PersistentUserManagementFactory<>();
        {
            UserFactory<ExampleFactoryA> userFactory = new UserFactory<>();
            userFactory.name.set("1");
            persistentUserManagementFactory.users.add(userFactory);
        }
        {
            UserFactory<ExampleFactoryA> userFactory = new UserFactory<>();
            userFactory.name.set("1");
            persistentUserManagementFactory.users.add(userFactory);
        }
        Assertions.assertEquals(1,persistentUserManagementFactory.internal().validateFlat().size());
    }


}