package io.github.factoryfx.javafx.distribution.launcher.downloadserver;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.javafx.distribution.launcher.rest.DistributionClientDownloadResourceFactory;
import io.github.factoryfx.jetty.JettyServerBuilder;
import org.eclipse.jetty.server.Server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class DistributionClientDownloadServerTest {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        FactoryTreeBuilder<Server,DownloadTestServerFactory> builder = new FactoryTreeBuilder<>(DownloadTestServerFactory.class, ctx -> {
            return new JettyServerBuilder<DownloadTestServerFactory>().
                    withHost("localhost").withPort(43654).withResource(ctx.get(SpecificDistributionClientDownloadResourceFactory.class)).
                    buildTo(new DownloadTestServerFactory());
        });
        builder.addFactory(DistributionClientDownloadResourceFactory.class, Scope.SINGLETON, ctx -> {
            DistributionClientDownloadResourceFactory<DownloadTestServerFactory> resource = new DistributionClientDownloadResourceFactory<>();
            resource.distributionClientZipPath.set("src/test/java/io/github/factoryfx/javafx/distribution/launcher/downloadserver/dummy.zip");
            return resource;
        });

        builder.microservice().build().start();
//
//
//        DistributionClientDownloadServerFactory distributionClientDownloadServerFactory=new DistributionClientDownloadServerFactory();
//        distributionClientDownloadServerFactory.port.set(43654);
//        distributionClientDownloadServerFactory.host.set("localhost");
//        distributionClientDownloadServerFactory.distributionClientBasePath.set("src/test/java/io/github/factoryfx/javafx/distribution/launcher/downloadserver");
//        distributionClientDownloadServerFactory.internalFactory().instance();
//        distributionClientDownloadServerFactory.internalFactory().start();

        try {
            java.awt.Desktop.getDesktop().browse(new URI("http://localhost:43654/downloadDistributionClient"));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}