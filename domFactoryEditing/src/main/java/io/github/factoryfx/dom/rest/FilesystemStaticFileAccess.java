package io.github.factoryfx.dom.rest;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FilesystemStaticFileAccess implements StaticFileAccess {
    public String basePath;
    public FilesystemStaticFileAccess(String basePath) {
        this.basePath=basePath;
    }

    public byte[] getFile(String path){
        try (InputStream inputStream= new FileInputStream(basePath+path)){
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
