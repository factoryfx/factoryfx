package de.factoryfx.user.persistent;

import de.factoryfx.factory.PolymorphicFactory;
import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.user.User;
import de.factoryfx.user.UserManagement;

public class PersistentUserManagementFactory<V> extends PolymorphicFactoryBase<UserManagement,V>  {
    public final FactoryReferenceListAttribute<User,UserFactory<V>> users = new FactoryReferenceListAttribute<User,UserFactory<V>>().setupUnsafe(UserFactory.class).en("users").de("Benutzer");

    @Override
    public PersistentUserManagement createImpl() {
        return new PersistentUserManagement(users.instances());
    }

    public PersistentUserManagementFactory(){
        config().setDisplayTextProvider(() -> "user management");
    }

    @Override
    public Class<UserManagement> getLiveObjectClass() {
        return UserManagement.class;
    }
}
