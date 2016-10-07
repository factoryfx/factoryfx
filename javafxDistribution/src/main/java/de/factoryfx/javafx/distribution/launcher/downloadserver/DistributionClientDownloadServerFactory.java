package de.factoryfx.javafx.distribution.launcher.downloadserver;

import java.util.Optional;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LifecycleNotifier;

public class DistributionClientDownloadServerFactory<V> extends FactoryBase<DistributionClientDownloadServer,V> {

    public final StringAttribute host = new StringAttribute(new AttributeMetadata().labelText("host"));
    public final IntegerAttribute port = new IntegerAttribute(new AttributeMetadata().labelText("port"));
    public final StringAttribute distributionClientBasePath = new StringAttribute(new AttributeMetadata().labelText("distributionClientBasePath"));

    @Override
    protected DistributionClientDownloadServer createImp(Optional<DistributionClientDownloadServer> previousLiveObject, LifecycleNotifier<V> lifecycleNotifier) {
        return new DistributionClientDownloadServer<>(host.get(),port.get(),distributionClientBasePath.get(),lifecycleNotifier);
    }
}
