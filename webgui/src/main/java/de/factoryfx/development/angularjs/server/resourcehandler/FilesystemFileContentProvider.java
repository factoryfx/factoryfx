package de.factoryfx.development.angularjs.server.resourcehandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesystemFileContentProvider implements FileContentProvider{
    private final Path webappFolder;

    public FilesystemFileContentProvider(Path webappFolder) {
        this.webappFolder = webappFolder;
    }

    @Override
    public boolean containsFile(String file) {
        return Files.exists(toFile(file));
    }

    @Override
    public byte[] getFile(String file) {
        return readFile(toFile(file).toFile());
    }

    private Path toFile(String file){
        return Paths.get(webappFolder.toString() + "/"+ file);
    }

    private byte[] readFile (File file){
        try {
            return com.google.common.io.Files.toByteArray(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
