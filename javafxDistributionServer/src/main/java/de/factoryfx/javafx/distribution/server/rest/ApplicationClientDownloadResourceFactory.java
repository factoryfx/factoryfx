package de.factoryfx.javafx.distribution.server.rest;

import java.io.File;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;

public class ApplicationClientDownloadResourceFactory<V,R extends FactoryBase<?,V,R>> extends FactoryBase<ApplicationClientDownloadResource,V,R> {
    public final StringAttribute guiZipFile = new StringAttribute().de("Datei für UI").en("File containing UI");

    public ApplicationClientDownloadResourceFactory() {
        config().setDisplayTextProvider(() -> "DownloadResource:"+guiZipFile.get());
        configLiveCycle().setCreator(() -> new ApplicationClientDownloadResource(new File(guiZipFile.get())));
    }

}
