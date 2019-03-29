package io.github.factoryfx.factory.attribute.types;

/**
 * File content as byte[] and metadata for file selection
 */
public class FileContentAttribute extends ByteArrayAttributeBase<FileContentAttribute> {
    private String fileExtension;

    /**
     * ui hint what files should be stored in the attribute
     * @param fileExtension fileExtension e.g.: jks (for example.jks files)
     * @return self
     */
    public FileContentAttribute fileExtension(String fileExtension){
        this.fileExtension=fileExtension;
        return this;
    }

    public String internal_getFileExtension(){
        return this.fileExtension;
    }




}
