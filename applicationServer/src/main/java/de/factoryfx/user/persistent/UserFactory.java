package de.factoryfx.user.persistent;

import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.attribute.types.LocaleAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.attribute.types.StringListAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.user.User;

public class UserFactory<V> extends SimpleFactoryBase<User,V> {
    /**key is static and not be part of the factory to keep it secret*/
    public static String passwordKey;

    public final StringAttribute name= new StringAttribute().en("name").de("Name");
    public final EncryptedStringAttribute password= new EncryptedStringAttribute().en("password").de("Passwort");
    public final LocaleAttribute locale= new LocaleAttribute().en("locale").de("Sprache");
    public final StringListAttribute permissons= new StringListAttribute().en("permissions").de("Rechte");

    @Override
    public User createImpl() {
        if (passwordKey==null){
            throw new IllegalStateException("missing passwordKey (you could create one with EncryptedStringAttribute), should be constant therefore don't create the key dynamic");
        }
        return new User(name.get(),password.decrypt(passwordKey),locale.get(),permissons.get());
    }

    public UserFactory(){
        config().setDisplayTextProvider(name::get,name);
    }
}
