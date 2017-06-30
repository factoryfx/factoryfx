package de.factoryfx.user.persistent;

import de.factoryfx.data.Data;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.factory.PolymorphicFactory;
import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.user.User;
import de.factoryfx.user.UserManagement;
import javafx.collections.ObservableList;

import java.util.HashSet;
import java.util.List;

public class PersistentUserManagementFactory<V> extends PolymorphicFactoryBase<UserManagement,V>  {
    public final FactoryReferenceListAttribute<User,UserFactory<V>> users = new FactoryReferenceListAttribute<User,UserFactory<V>>().setupUnsafe(UserFactory.class).en("users").de("Benutzer");

    @Override
    public PersistentUserManagement createImpl() {
        return new PersistentUserManagement(users.instances());
    }

    public PersistentUserManagementFactory(){
        config().setDisplayTextProvider(() -> "user management");

        users.validation(new Validation<List<UserFactory<V>>>() {
            @Override
            public LanguageText getValidationDescription() {
                return new LanguageText().en("user name is not unique");
            }
            @Override
            public boolean validate(List<UserFactory<V>> value) {
                HashSet<String> set = new HashSet<>();
                for (UserFactory user : users) {
                    if (!set.add(user.name.get())) {
                        return false;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public Class<UserManagement> getLiveObjectClass() {
        return UserManagement.class;
    }
}
