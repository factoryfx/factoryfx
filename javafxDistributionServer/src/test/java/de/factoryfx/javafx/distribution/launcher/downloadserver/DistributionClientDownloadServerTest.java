package de.factoryfx.javafx.distribution.launcher.downloadserver;

import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.javafx.distribution.launcher.rest.DistributionClientDownloadResourceFactory;
import de.factoryfx.jetty.JettyServerBuilder;
import de.factoryfx.factory.builder.MicroserviceBuilder;
import org.eclipse.jetty.server.Server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class DistributionClientDownloadServerTest {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        FactoryTreeBuilder<Void, Server,DownloadTestServerFactory,Void> builder = new FactoryTreeBuilder<>(DownloadTestServerFactory.class);
        builder.addFactory(DownloadTestServerFactory.class, Scope.SINGLETON, ctx -> {
            return new JettyServerBuilder<>(new DownloadTestServerFactory()).
                    withHost("localhost").widthPort(43654).withResource(ctx.get(SpecificDistributionClientDownloadResourceFactory.class)).
                    build();
        });
        builder.addFactory(DistributionClientDownloadResourceFactory.class, Scope.SINGLETON, ctx -> {
            DistributionClientDownloadResourceFactory<Void, DownloadTestServerFactory> resource = new DistributionClientDownloadResourceFactory<>();
            resource.distributionClientZipPath.set("src/test/java/de/factoryfx/javafx/distribution/launcher/downloadserver/dummy.zip");
            return resource;
        });

        builder.microservice().withInMemoryStorage().build().start();
//
//
//        DistributionClientDownloadServerFactory distributionClientDownloadServerFactory=new DistributionClientDownloadServerFactory();
//        distributionClientDownloadServerFactory.port.set(43654);
//        distributionClientDownloadServerFactory.host.set("localhost");
//        distributionClientDownloadServerFactory.distributionClientBasePath.set("src/test/java/de/factoryfx/javafx/distribution/launcher/downloadserver");
//        distributionClientDownloadServerFactory.internalFactory().instance();
//        distributionClientDownloadServerFactory.internalFactory().start();

        try {
            java.awt.Desktop.getDesktop().browse(new URI("http://localhost:43654/downloadDistributionClient"));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}