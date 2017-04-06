package de.factoryfx.javascript.data.attributes.types;

import com.google.common.io.ByteStreams;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.DefaultExterns;
import com.google.javascript.jscomp.SourceFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Externs {

    static final List<SourceFile> externs;
    static {
        try {
            InputStream input = Compiler.class.getResourceAsStream("/externs.zip");
            if (input == null) {
                input = Compiler.class.getResourceAsStream("externs.zip");
            }
            ZipInputStream zip = new ZipInputStream(input);
            String envPrefix = CompilerOptions.Environment.CUSTOM.toString().toLowerCase() + "/";
            Map<String, SourceFile> mapFromExternsZip = new HashMap<>();
            for (ZipEntry entry = null; (entry = zip.getNextEntry()) != null; ) {
                String filename = entry.getName();

                if (filename.contains("/")) {
                    if (!filename.startsWith(envPrefix)) {
                        continue;
                    }
                    filename = filename.substring(envPrefix.length());
                }

                BufferedInputStream entryStream = new BufferedInputStream(
                        ByteStreams.limit(zip, entry.getSize()));
                mapFromExternsZip.put(filename,
                        SourceFile.fromInputStream(
                                "externs.zip//" + filename,
                                entryStream,
                                UTF_8));
            }
            externs = Collections.unmodifiableList(DefaultExterns.prepareExterns(CompilerOptions.Environment.CUSTOM, mapFromExternsZip));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<SourceFile> get() {
        return externs;
    }

}
