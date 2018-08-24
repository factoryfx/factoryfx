package de.factoryfx.javafx.distribution.launcher.downloadserver;

import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.distribution.launcher.rest.DistributionClientDownloadResource;
import de.factoryfx.javafx.distribution.launcher.rest.DistributionClientDownloadResourceFactory;
import de.factoryfx.jetty.JettyServerFactory;

import java.util.Arrays;
import java.util.List;


public class DownloadTestServerFactory extends JettyServerFactory<Void,DownloadTestServerFactory>{
    @SuppressWarnings("unchecked")
     public final FactoryReferenceAttribute<DistributionClientDownloadResource,DistributionClientDownloadResourceFactory<Void,DownloadTestServerFactory>> resource =
            FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(DistributionClientDownloadResourceFactory.class));

     @Override
     protected List<Object> getResourcesInstances() {
         return List.of(resource.instance());
     }
}
