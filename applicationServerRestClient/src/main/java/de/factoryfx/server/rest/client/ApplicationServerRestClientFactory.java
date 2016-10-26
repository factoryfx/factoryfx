package de.factoryfx.server.rest.client;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;

public class ApplicationServerRestClientFactory<V,T extends FactoryBase<?,V>> extends FactoryBase<ApplicationServerRestClient<V,T>,V> {
    public final StringAttribute host=new StringAttribute(new AttributeMetadata().labelText("host"));
    public final IntegerAttribute port=new IntegerAttribute(new AttributeMetadata().labelText("port"));
    public final StringAttribute path=new StringAttribute(new AttributeMetadata().labelText("path")).defaultValue("adminui");
    public final ObjectValueAttribute<Class<T>> factoryRootClass = new ObjectValueAttribute<>(new AttributeMetadata().en("factoryRootClass"));

    public final StringAttribute httpAuthenticationUser=new StringAttribute(new AttributeMetadata().labelText("httpAuthenticationUser"));
    public final StringAttribute httpAuthenticationPassword=new StringAttribute(new AttributeMetadata().labelText("httpAuthenticationPassword"));

    @Override
    public LiveCycleController<ApplicationServerRestClient<V,T>, V> createLifecycleController() {
        return () -> new ApplicationServerRestClient<>(host.get(),port.get(),path.get(),false,factoryRootClass.get(),httpAuthenticationUser.get(),httpAuthenticationPassword.get());
    }
}
