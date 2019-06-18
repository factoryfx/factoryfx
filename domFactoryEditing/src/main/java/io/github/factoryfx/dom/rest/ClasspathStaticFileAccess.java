package io.github.factoryfx.dom.rest;


import java.io.IOException;
import java.io.InputStream;

public class ClasspathStaticFileAccess implements StaticFileAccess {
    public byte[] getFile(String path){
        try (InputStream inputStream= getClass().getResourceAsStream("/js/"+path)){
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
