package de.factoryfx.javafx.distribution.launcher.downloadserver;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.BooleanAttribute;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;

public class DistributionClientDownloadServerFactory<V> extends FactoryBase<DistributionClientDownloadServer,V> {


    public final StringAttribute host = new StringAttribute(new AttributeMetadata().de("host").en("host"));
    public final IntegerAttribute port = new IntegerAttribute(new AttributeMetadata().de("port").en("port"));
    public final StringAttribute distributionClientBasePath = new StringAttribute(new AttributeMetadata().labelText("distributionClientBasePath"));
    public final BooleanAttribute directoriesListed = new BooleanAttribute(new AttributeMetadata().labelText("directoriesListed"));

    public DistributionClientDownloadServerFactory() {
        config().setDisplayTextProvider(() -> "http://"+host.get()+":"+port.get());

        configLiveCycle().setCreator(() -> new DistributionClientDownloadServer<V>(host.get(),port.get(),distributionClientBasePath.get(),directoriesListed.get()));

        configLiveCycle().setStarter(newLiveObject -> newLiveObject.start());
        configLiveCycle().setDestroyer(previousLiveObject -> previousLiveObject.stop());
    }
}
