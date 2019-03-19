package io.github.factoryfx.javafx.distribution.launcher.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.File;

@Path("/downloadDistributionClient")
public class DistributionClientDownloadResource {

    private final String distributionClientZipPath;

    public DistributionClientDownloadResource(String distributionClientZipPath) {
        this.distributionClientZipPath = distributionClientZipPath;
    }

    @GET
    @Produces("application/zip")
    public Response getClient() {
        return Response.ok(new File(distributionClientZipPath), "application/zip").build();
    }
}
