package de.factoryfx.user.persistent;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.attribute.types.LocaleAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.attribute.types.StringListAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.user.User;

public class UserFactory<V> extends SimpleFactoryBase<User,V> {
    private static final AttributeMetadata NAME = new AttributeMetadata().en("name").de("Name");
    private static final AttributeMetadata PASSWORD = new AttributeMetadata().en("name").de("Name");
    private static final AttributeMetadata LOCLAE = new AttributeMetadata().en("locale").de("sprache");
    private static final AttributeMetadata PERMISSONS = new AttributeMetadata().en("permissons").de("Rechte");

    public final StringAttribute name= new StringAttribute(NAME);
    public final EncryptedStringAttribute password= new EncryptedStringAttribute(PASSWORD);
    public final LocaleAttribute locale= new LocaleAttribute(LOCLAE);
    public final StringListAttribute permissons= new StringListAttribute(PERMISSONS);

    @Override
    public User createImpl() {
        return new User(name.get(),password.decrypt(System.getProperty("factoryfx.persistentUserManagement.key")),locale.get(),permissons.get());
    }
}
