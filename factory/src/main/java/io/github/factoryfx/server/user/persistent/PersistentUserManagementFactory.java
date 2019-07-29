package io.github.factoryfx.server.user.persistent;

import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.ValidationResult;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.server.user.User;
import io.github.factoryfx.server.user.UserManagement;

import java.util.HashSet;

public class PersistentUserManagementFactory<R extends FactoryBase<?,R>> extends PolymorphicFactoryBase<UserManagement,R> {

    public final FactoryListAttribute<User, UserFactory<R>> users = new FactoryListAttribute<User, UserFactory<R>>().en("users").de("Benutzer").userNotSelectable();

    @Override
    protected PersistentUserManagement createImpl() {
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
