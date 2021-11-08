package io.github.factoryfx.javafx.distribution.launcher.rest;

import java.io.File;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

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
