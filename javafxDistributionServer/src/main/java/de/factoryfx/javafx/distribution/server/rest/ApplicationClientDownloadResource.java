package de.factoryfx.javafx.distribution.server.rest;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

@Path("/download")
public class ApplicationClientDownloadResource {

    final File guiZipFile;

    public ApplicationClientDownloadResource(File guiZipFile) {
        this.guiZipFile = guiZipFile;
    }

    public File getGuiFile() {
        return guiZipFile;
    }

    @SuppressWarnings("deprecation")// can't easily change Hashing function
    public boolean needUpdate(String fileHash) {
        try {
            String md5FileHash = Files.asByteSource(guiZipFile).hash(Hashing.md5()).toString();
            return !md5FileHash.equals(fileHash);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("/checkVersion")
    @Produces(MediaType.TEXT_PLAIN)
    public String updateConfiguration(@QueryParam("fileHash")String  fileHash) {
        return Boolean.toString(needUpdate(fileHash));
    }

    @GET
    @Produces("application/zip")
    public Response getConfiguration() {
        return Response.ok(guiZipFile, "application/zip").build();
    }
}
