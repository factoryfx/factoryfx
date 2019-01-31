package de.factoryfx.data.storage.migration;

public class GeneralStorageMetadataBuilder {

    public static GeneralStorageFormat build(){
        return new GeneralStorageFormat(1, 0);
    }

    public static GeneralStorageFormat build(int customFormatVersion){
        return new GeneralStorageFormat(1, customFormatVersion);
    }

}
