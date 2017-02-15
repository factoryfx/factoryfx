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

/** path defined in server and not with annotation*/
@Path("/")
public class DownloadResource {

    final File guiZipFile;

    public DownloadResource(File guiZipFile) {
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

    @GET
    @Path("/checkVersion")
    @Produces(MediaType.TEXT_PLAIN)
    public String updateConfiguration(@QueryParam("fileHash")String  fileHash) {
        Boolean needUpdate=needUpdate(fileHash);
        return needUpdate.toString();
    }

    @GET
    @Produces("application/zip")
    public Response getConfiguration() {
        return Response.ok(guiZipFile, "application/zip").build();
    }
}
