package de.factoryfx.user.persistent;

import de.factoryfx.data.attribute.types.*;
import de.factoryfx.data.validation.ObjectRequired;
import de.factoryfx.data.validation.StringRequired;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.user.User;

public class UserFactory<V> extends SimpleFactoryBase<User,V> {
    /**key is static and not part of the factory to keep the key secret*/
    public static String passwordKey;

    public final StringAttribute name= new StringAttribute().en("name").de("Name").validation(StringRequired.VALIDATION);
    public final PasswordAttribute password= new PasswordAttribute().en("password").de("Passwort").hash(s -> new PasswordHash().hash(s)).validation(new ObjectRequired<>());
    public final LocaleAttribute locale= new LocaleAttribute().en("locale").de("Sprache");
    public final StringListAttribute permissons= new StringListAttribute().en("permissions").de("Rechte");

    @Override
    public User createImpl() {
        if (passwordKey==null){
            throw new IllegalStateException("missing passwordKey (you could create one with EncryptedStringAttribute), should be constant therefore don't create the key dynamic");
        }
        return new User(name.get(),password.get().decrypt(passwordKey),locale.get(),permissons.get());
    }

    public UserFactory(){
        config().setDisplayTextProvider(name::get,name);
    }
}
