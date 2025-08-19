package io.github.factoryfx.javafx.distribution.launcher.downloadserver;

import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.javafx.distribution.launcher.rest.DistributionClientDownloadResourceFactory;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class DistributionClientDownloadServerTest {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->
                jetty.withHost("localhost").withPort(43654).withResource(new FactoryTemplateId<>(SpecificDistributionClientDownloadResourceFactory.class))
        );
        builder.addFactory(DistributionClientDownloadResourceFactory.class, Scope.SINGLETON, ctx -> {
            DistributionClientDownloadResourceFactory<JettyServerRootFactory> resource = new DistributionClientDownloadResourceFactory<>();
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