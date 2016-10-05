package de.factoryfx.javafx.distribution.downloadserver;

import java.util.Optional;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;

public class DistributionClientDownloadServerFactory extends FactoryBase<DistributionClientDownloadServer> {

    StringAttribute host = new StringAttribute(new AttributeMetadata().labelText("host"));
    IntegerAttribute port = new IntegerAttribute(new AttributeMetadata().labelText("port"));
    StringAttribute distributionClientBasePath = new StringAttribute(new AttributeMetadata().labelText("distributionClientBasePath"));

    @Override
    protected DistributionClientDownloadServer createImp(Optional<DistributionClientDownloadServer> previousLiveObject) {
        return new DistributionClientDownloadServer(host.get(),port.get(),distributionClientBasePath.get());
    }
}
