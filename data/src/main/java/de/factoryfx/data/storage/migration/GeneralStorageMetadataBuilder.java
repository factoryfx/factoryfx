package de.factoryfx.data.storage.migration;

public class GeneralStorageMetadataBuilder {

    public static GeneralStorageMetadata build(){
        return new GeneralStorageMetadata(1, 0);
    }

    public static GeneralStorageMetadata build(int customFormatVersion){
        return new GeneralStorageMetadata(1, customFormatVersion);
    }

}
