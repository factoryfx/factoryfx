package de.factoryfx.javafx.distribution.launcher.downloadserver;

import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;

public class DistributionClientDownloadServerFactory<V,R extends FactoryBase<?,V,R>> extends FactoryBase<DistributionClientDownloadServer,V,R> {


    public final StringAttribute host = new StringAttribute().de("host").en("host");
    public final IntegerAttribute port = new IntegerAttribute().de("port").en("port");
    public final StringAttribute distributionClientBasePath = new StringAttribute().labelText("distributionClientBasePath");
    public final BooleanAttribute directoriesListed = new BooleanAttribute().labelText("directoriesListed");

    public DistributionClientDownloadServerFactory() {
        config().setDisplayTextProvider(() -> "http://"+host.get()+":"+port.get());

        configLiveCycle().setCreator(() -> new DistributionClientDownloadServer(host.get(),port.get(),distributionClientBasePath.get(),directoriesListed.get()));

        configLiveCycle().setStarter(DistributionClientDownloadServer::start);
        configLiveCycle().setDestroyer(DistributionClientDownloadServer::stop);
    }
}
