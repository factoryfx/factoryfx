package de.factoryfx.javafx.distribution.server.rest;

import java.io.File;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;

public class DownloadResourceFactory<V> extends FactoryBase<DownloadResource,V> {
    public final StringAttribute guiZipFile = new StringAttribute(new AttributeMetadata().de("Datei für UI").en("File containing UI"));

    public DownloadResourceFactory() {
        config().setDisplayTextProvider(() -> "DownloadResource:"+guiZipFile.get());
        configLiveCycle().setCreator(() -> new DownloadResource(new File(guiZipFile.get())));
    }

}
