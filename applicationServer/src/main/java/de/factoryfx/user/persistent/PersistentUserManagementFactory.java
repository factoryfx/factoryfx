package de.factoryfx.user.persistent;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.user.User;
import de.factoryfx.user.UserManagementFactory;

public class PersistentUserManagementFactory<V> extends UserManagementFactory<V> {

    public final FactoryReferenceListAttribute<User,UserFactory<V>> users = new FactoryReferenceListAttribute<>(new AttributeMetadata().en("users").de("Benutzer"),UserFactory.class);

    @Override
    public PersistentUserManagement createImpl() {
        return new PersistentUserManagement(users.instances());
    }
}
