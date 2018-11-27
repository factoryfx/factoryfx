package de.factoryfx.server.user.persistent;

import de.factoryfx.data.attribute.types.LocaleAttribute;
import de.factoryfx.data.attribute.types.PasswordAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.attribute.types.StringListAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.server.user.User;

public class UserFactory<V,R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<User,V,R> {
    /**key is static and not part of the factory to keep the key secret*/
    public static String passwordKey;

    public final StringAttribute name = new StringAttribute().en("name").de("Name");
    public final PasswordAttribute password = new PasswordAttribute().en("password").de("Passwort").hash(s -> new PasswordHash().hash(s));
    public final LocaleAttribute locale = new LocaleAttribute().en("locale").de("Sprache");
    public final StringListAttribute permissons = new StringListAttribute().en("permissions").de("Rechte").nullable();

    @Override
    public User createImpl() {
        if (passwordKey==null){
            throw new IllegalStateException("missing passwordKey (you could create one with EncryptedStringAttribute), should be constant therefore don't create the key dynamically");
        }
        return new User(name.get(),password.get().decrypt(passwordKey),locale.get(),permissons.get());
    }

    public UserFactory(){
        config().setDisplayTextProvider(name::get,name);
    }
}
