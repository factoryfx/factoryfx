package de.factoryfx.user.persistent;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.attribute.types.LocaleAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.attribute.types.StringListAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.user.User;

public class UserFactory<V> extends SimpleFactoryBase<User,V> {
    /**key is static and not be part of the factory to keep it secret*/
    public static String passwordKey;

    private static final AttributeMetadata NAME = new AttributeMetadata().en("name").de("Name");
    private static final AttributeMetadata PASSWORD = new AttributeMetadata().en("password").de("Passwort");
    private static final AttributeMetadata LOCLAE = new AttributeMetadata().en("locale").de("Sprache");
    private static final AttributeMetadata PERMISSIONS = new AttributeMetadata().en("permissions").de("Rechte");

    public final StringAttribute name= new StringAttribute(NAME);
    public final EncryptedStringAttribute password= new EncryptedStringAttribute(PASSWORD);
    public final LocaleAttribute locale= new LocaleAttribute(LOCLAE);
    public final StringListAttribute permissons= new StringListAttribute(PERMISSIONS);

    @Override
    public User createImpl() {
        if (passwordKey==null){
            throw new IllegalStateException("missing passwordKey (you could create one with EncryptedStringAttribute), should be constant therefore don't create the key dynamic");
        }
        return new User(name.get(),password.decrypt(passwordKey),locale.get(),permissons.get());
    }

    public UserFactory(){
        config().setDisplayTextProvider(name::get);
    }
}
