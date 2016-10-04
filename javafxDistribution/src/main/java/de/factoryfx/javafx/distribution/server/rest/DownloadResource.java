package de.factoryfx.javafx.distribution.server.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.factoryfx.javafx.distribution.server.GuiFileService;

@Path("/") /** path defined in {@link de.scoopsoftware.xtc.ticketproxy.configuration.ConfigurationServer}*/
public class DownloadResource {

    private final GuiFileService guiFileService;

    public DownloadResource(GuiFileService guiFileService) {
        this.guiFileService = guiFileService;
    }


    @GET
    @Path("/checkVersion")
    @Produces(MediaType.TEXT_PLAIN)
    public String updateConfiguration(@QueryParam("fileHash")String  fileHash) {
        Boolean needUpdate=guiFileService.needUpdate(fileHash);
        return needUpdate.toString();
    }

    @GET
    @Produces("application/zip")
    public Response getConfiguration() {
        return Response.ok(guiFileService.getGuiFile(), "application/zip").build();
    }
}
