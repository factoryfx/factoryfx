package io.github.factoryfx.factory.builder;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileSystemStorageTestUtil {
    //Patch class names in json files
    public static void patchClassName(Path folder){
        try {
            List<Path> filesTPatch = new ArrayList<>();
            filesTPatch.add(folder.resolve("currentFactory.json"));
            filesTPatch.add(folder.resolve("currentFactory_metadata.json"));
            try (
                    DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder.resolve("history"))) {
                for (Path path : directoryStream) {
                    filesTPatch.add(path);
                }
            }
            for (Path file : filesTPatch) {
                String currentFactorymetadata= null;

                 currentFactorymetadata = Files.readString(file);
                currentFactorymetadata=currentFactorymetadata.replace("Old","");
                Files.writeString(file,currentFactorymetadata);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
