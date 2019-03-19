package io.github.factoryfx.server.user.persistent;

import io.github.factoryfx.data.util.LanguageText;
import io.github.factoryfx.data.validation.ValidationResult;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import io.github.factoryfx.server.user.User;
import io.github.factoryfx.server.user.UserManagement;

import java.util.HashSet;

public class PersistentUserManagementFactory<R extends FactoryBase<?,R>> extends PolymorphicFactoryBase<UserManagement,R> {
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
