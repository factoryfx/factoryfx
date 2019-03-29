package io.github.factoryfx.server.user.persistent;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.github.factoryfx.factory.attribute.types.LocaleAttribute;
import io.github.factoryfx.factory.attribute.types.PasswordAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.attribute.types.StringListAttribute;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.server.user.User;

public class UserFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<User,R> {
    /**key is static and not part of the factory to keep the key secret*/
    public static String passwordKey;

    public final StringAttribute name = new StringAttribute().en("name").de("Name");
    public final PasswordAttribute password = new PasswordAttribute().en("password").de("Passwort").hash(s -> new PasswordHash().hash(s));
    public final LocaleAttribute locale = new LocaleAttribute().en("locale").de("Sprache");
    @JsonAlias({"permissions", "permissons"})//for compatibility
    public final StringListAttribute permissions = new StringListAttribute().en("permissions").de("Rechte").nullable();

    @Override
    public User createImpl() {
        if (passwordKey==null){
            throw new IllegalStateException("missing passwordKey (you could create one with EncryptedStringAttribute), should be constant therefore don't create the key dynamically");
        }
        return new User(name.get(),password.get().decrypt(passwordKey),locale.get(),permissions.get());
    }

    public UserFactory(){
        config().setDisplayTextProvider(name::get,name);
    }
}
