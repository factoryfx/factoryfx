package de.factoryfx.javafx.distribution.launcher.downloadserver;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.BooleanAttribute;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;

public class DistributionClientDownloadServerFactory<V> extends FactoryBase<DistributionClientDownloadServer,V> {

    public DistributionClientDownloadServerFactory(){
        setDisplayTextProvider(() -> "http://"+host.get()+"+"+port.get());
    }

    public final StringAttribute host = new StringAttribute(new AttributeMetadata().de("host").en("host"));
    public final IntegerAttribute port = new IntegerAttribute(new AttributeMetadata().de("port").en("port"));
    public final StringAttribute distributionClientBasePath = new StringAttribute(new AttributeMetadata().labelText("distributionClientBasePath"));
    public final BooleanAttribute directoriesListed = new BooleanAttribute(new AttributeMetadata().labelText("directoriesListed"));

    @Override
    public LiveCycleController<DistributionClientDownloadServer, V> createLifecycleController() {
        return new LiveCycleController<DistributionClientDownloadServer, V>() {
            @Override
            public DistributionClientDownloadServer create() {
                return new DistributionClientDownloadServer<>(host.get(),port.get(),distributionClientBasePath.get(),directoriesListed.get());
            }

            @Override
            public void start(DistributionClientDownloadServer newLiveObject) {
                newLiveObject.start();
            }
            @Override
            public void destroy(DistributionClientDownloadServer previousLiveObject) {
                previousLiveObject.stop();
            };
        };
    }
}
