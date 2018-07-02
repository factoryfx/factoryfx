package de.factoryfx.javafx.distribution.launcher.downloadserver;

import de.factoryfx.factory.builder.FactoryContext;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.javafx.distribution.launcher.rest.DistributionClientDownloadResourceFactory;
import de.factoryfx.jetty.HttpServerConnectorFactory;
import de.factoryfx.server.MicroserviceBuilder;
import net.bytebuddy.asm.Advice;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Function;


public class DistributionClientDownloadServerTest {

    public static void main(String[] args) {

        FactoryTreeBuilder<DownloadTestServerFactory> builder = new FactoryTreeBuilder<>(DownloadTestServerFactory.class);
        builder.addFactory(DownloadTestServerFactory.class, Scope.SINGLETON, ctx -> {
            DownloadTestServerFactory serverFactory = new DownloadTestServerFactory();
            HttpServerConnectorFactory<Void, DownloadTestServerFactory> httpServerConnectorFactory = new HttpServerConnectorFactory<>();
            httpServerConnectorFactory.port.set(43654);
            httpServerConnectorFactory.host.set("localhost");
            serverFactory.connectors.add(httpServerConnectorFactory);

            DistributionClientDownloadResourceFactory<Void, DownloadTestServerFactory> resource = new DistributionClientDownloadResourceFactory<>();
            resource.distributionClientZipPath.set("src/test/java/de/factoryfx/javafx/distribution/launcher/downloadserver/dummy.zip");
            serverFactory.resource.set(resource);
            return serverFactory;
        });

        MicroserviceBuilder.buildInMemoryMicroservice(builder.buildTree()).start();
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