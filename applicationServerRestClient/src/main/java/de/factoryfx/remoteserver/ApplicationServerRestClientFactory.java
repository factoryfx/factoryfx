package de.factoryfx.remoteserver;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;

public class ApplicationServerRestClientFactory<V,T extends FactoryBase<?,V>> extends FactoryBase<ApplicationServerRestClient<V,T>,V> {
    public final StringAttribute host=new StringAttribute(new AttributeMetadata().labelText("host"));
    public final IntegerAttribute port=new IntegerAttribute(new AttributeMetadata().labelText("port"));
    public final ObjectValueAttribute<Class<T>> factoryRootClass = new ObjectValueAttribute<>(new AttributeMetadata().en("factoryRootClass"));

    @Override
    public LiveCycleController<ApplicationServerRestClient<V,T>, V> createLifecycleController() {
        return () -> new ApplicationServerRestClient<>(host.get(),port.get(),false,factoryRootClass.get());
    }
}
