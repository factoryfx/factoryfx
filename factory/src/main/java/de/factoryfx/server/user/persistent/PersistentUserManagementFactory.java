package de.factoryfx.server.user.persistent;

import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.ValidationResult;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.server.user.User;
import de.factoryfx.server.user.UserManagement;
import de.factoryfx.server.user.persistent.UserFactory;

import java.util.HashSet;

public class PersistentUserManagementFactory<R extends FactoryBase<?,R>> extends PolymorphicFactoryBase<UserManagement,R>  {
    @SuppressWarnings("unchecked")
    public final FactoryReferenceListAttribute<User, UserFactory<R>> users = FactoryReferenceListAttribute.create(new FactoryReferenceListAttribute<>(UserFactory.class).en("users").de("Benutzer").userNotSelectable());

    @Override
    public PersistentUserManagement createImpl() {
        return new PersistentUserManagement(users.instances());
    }

    public PersistentUserManagementFactory(){
        config().setDisplayTextProvider(() -> "user management");

        users.validation(value -> {
            HashSet<String> set = new HashSet<>();
            LanguageText en = new LanguageText().en("user name is not unique");
            for (UserFactory user : users) {
                if (!set.add(user.name.get())) {
                    return new ValidationResult(true, en);
                }
            }
            return new ValidationResult(false, en);
        });
    }

    @Override
    public Class<UserManagement> getLiveObjectClass() {
        return UserManagement.class;
    }
}
