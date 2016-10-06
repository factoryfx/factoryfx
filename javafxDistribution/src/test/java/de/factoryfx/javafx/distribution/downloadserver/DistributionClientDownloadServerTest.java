package de.factoryfx.javafx.distribution.downloadserver;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import de.factoryfx.factory.Lifecycle;

public class DistributionClientDownloadServerTest {

    public static void main(String[] args) {


        DistributionClientDownloadServerFactory distributionClientDownloadServerFactory=new DistributionClientDownloadServerFactory();
        distributionClientDownloadServerFactory.port.set(43654);
        distributionClientDownloadServerFactory.host.set("localhost");
        distributionClientDownloadServerFactory.distributionClientBasePath.set("src/test/java/de/factoryfx/javafx/distribution/downloadserver");
        Lifecycle<Object> lifecycle = new Lifecycle<>();
        distributionClientDownloadServerFactory.createImp(Optional.empty(), lifecycle);
        lifecycle.start();

        try {
            java.awt.Desktop.getDesktop().browse(new URI("http://localhost:43654/dummy.zip"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}