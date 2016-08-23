package de.factoryfx.adminui.angularjs.server.resourcehandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class FilesystemFileContentProvider implements FileContentProvider{
    private final Path webappFolder;
    private final byte[] customisedCss;

    public FilesystemFileContentProvider(Path webappFolder,byte[] customisedCss) {
        this.webappFolder = webappFolder;
        this.customisedCss=customisedCss;
    }

    @Override
    public boolean containsFile(String file) {
        if (file.endsWith(CUSTOMISABLE_CSS)) {
            return true;
        }
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
        if (file.toString().endsWith(CUSTOMISABLE_CSS)) {
            return customisedCss;
        }

        if (file.toString().endsWith("index.html")){
            try (InputStream inputStream= new FileInputStream(file)) {
                Document doc = Jsoup.parse(inputStream, "UTF8", "/");
                replaceIndexHtmlPlaceholder(doc);
                return doc.html().getBytes(StandardCharsets.UTF_8);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }


        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
