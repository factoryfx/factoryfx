package io.github.factoryfx.javafx.distribution.server.rest;

import java.io.File;
import java.io.IOException;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/download")
public class ApplicationClientDownloadResource {

    protected final File guiZipFile;

    public ApplicationClientDownloadResource(File guiZipFile) {
        this.guiZipFile = guiZipFile;
    }

    public File getGuiFile() {
        return guiZipFile;
    }

    @SuppressWarnings("deprecation")// can't easily change Hashing function
    public boolean needUpdate(String fileHash) {
        try {
            String md5FileHash = Files.asByteSource(getGuiFile()).hash(Hashing.md5()).toString();
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
        return Response.ok(getGuiFile(), "application/zip").build();
    }
}
