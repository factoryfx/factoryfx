package io.github.factoryfx.javafx.distribution.launcher.rest;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FactoryBase;

public class DistributionClientDownloadResourceFactory<R extends FactoryBase<?,R>> extends FactoryBase<DistributionClientDownloadResource,R> {
    public final StringAttribute distributionClientZipPath = new StringAttribute().labelText("Distribution client zip path on the server");

    public DistributionClientDownloadResourceFactory() {
        config().setDisplayTextProvider(() -> "Distribution client download path:"+distributionClientZipPath.get());
        configLifeCycle().setCreator(() -> new DistributionClientDownloadResource(distributionClientZipPath.get()));
    }

}
