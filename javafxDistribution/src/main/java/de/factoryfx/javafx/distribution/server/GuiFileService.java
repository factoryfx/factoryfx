package de.factoryfx.javafx.distribution.server;

import java.io.File;
import java.io.IOException;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

public class GuiFileService {
    final File guiZipFile;
    public GuiFileService(File guiZipFile){
        this.guiZipFile = guiZipFile;

    }

    public File getGuiFile() {
        return guiZipFile;
    }

    public boolean needUpdate(String fileHash) {
        try {
            String md5FileHash = Files.hash(guiZipFile, Hashing.md5()).toString();
            return !md5FileHash.equals(fileHash);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
