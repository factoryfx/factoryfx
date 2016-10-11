package de.factoryfx.javafx.distribution.server;

import java.io.File;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;
import de.factoryfx.javafx.distribution.server.rest.DownloadResource;

public class UserInterfaceDistributionServerFactory<V> extends FactoryBase<UserInterfaceDistributionServer,V> {
    public final StringAttribute host = new StringAttribute(new AttributeMetadata().labelText("host"));
    public final IntegerAttribute port = new IntegerAttribute(new AttributeMetadata().labelText("port"));
    public final StringAttribute guiZipFile = new StringAttribute(new AttributeMetadata().labelText("port"));

    @Override
    public LiveCycleController<UserInterfaceDistributionServer, V> createLifecycleController() {
        return () -> new UserInterfaceDistributionServer(host.get(),port.get(),new DownloadResource(new File(guiZipFile.get())));
    }
}
