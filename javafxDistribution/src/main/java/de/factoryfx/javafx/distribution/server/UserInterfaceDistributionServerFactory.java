package de.factoryfx.javafx.distribution.server;

import java.io.File;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;
import de.factoryfx.javafx.distribution.server.rest.DownloadResource;

public class UserInterfaceDistributionServerFactory<V> extends FactoryBase<UserInterfaceDistributionServer,V> {
    public UserInterfaceDistributionServerFactory(){
        setDisplayTextProvider(() -> "http://"+host.get()+"+"+port.get());
    }

    public final StringAttribute host = new StringAttribute(new AttributeMetadata().de("host").en("host"));
    public final IntegerAttribute port = new IntegerAttribute(new AttributeMetadata().de("port").en("port"));
    public final StringAttribute guiZipFile = new StringAttribute(new AttributeMetadata().de("Datei für UI").en("File containing UI"));

    @Override
    public LiveCycleController<UserInterfaceDistributionServer, V> createLifecycleController() {
        return new LiveCycleController<UserInterfaceDistributionServer, V>() {
            @Override
            public UserInterfaceDistributionServer create() {
                return new UserInterfaceDistributionServer(host.get(),port.get(),new DownloadResource(new File(guiZipFile.get())));
            }

            @Override
            public void start(UserInterfaceDistributionServer newLiveObject) {
                newLiveObject.start();
            }

            @Override
            public void destroy(UserInterfaceDistributionServer previousLiveObject) {
                previousLiveObject.stop();
            }
        };


    }
}
