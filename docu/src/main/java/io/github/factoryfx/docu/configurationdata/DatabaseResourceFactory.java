package io.github.factoryfx.docu.configurationdata;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;

public class DatabaseResourceFactory  extends SimpleFactoryBase<DatabaseResource, RootFactory> {
    public final StringAttribute url = new StringAttribute();
    public final StringAttribute user = new StringAttribute();
    public final StringAttribute password = new StringAttribute();

    @Override
    public DatabaseResource createImpl() {
        return new DatabaseResource(url.get(),user.get(),password.get());
    }
}
