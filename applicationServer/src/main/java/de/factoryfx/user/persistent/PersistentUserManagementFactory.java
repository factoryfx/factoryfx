package de.factoryfx.user.persistent;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.user.User;

public class PersistentUserManagementFactory<V> extends SimpleFactoryBase<V,PersistentUserManagement>{

    public final FactoryReferenceListAttribute<User,UserFactory<V>> users = new FactoryReferenceListAttribute<>(new AttributeMetadata().en("users").de("Benutzer"),UserFactory.class);

    @Override
    public V createImpl() {
        return null;
    }
}
