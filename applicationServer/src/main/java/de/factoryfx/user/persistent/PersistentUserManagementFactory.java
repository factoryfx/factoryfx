package de.factoryfx.user.persistent;

import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.user.User;
import de.factoryfx.user.UserManagementFactory;

import java.util.function.Predicate;

public class PersistentUserManagementFactory<V> extends UserManagementFactory<V> {
    public final FactoryReferenceListAttribute<User,UserFactory<V>> users = new FactoryReferenceListAttribute<User,UserFactory<V>>().setupUnsafe(UserFactory.class).en("users").de("Benutzer");

    @Override
    public PersistentUserManagement createImpl() {
        return new PersistentUserManagement(users.instances());
    }

    public PersistentUserManagementFactory(){
        config().setDisplayTextProvider(() -> "user management");
    }

}
