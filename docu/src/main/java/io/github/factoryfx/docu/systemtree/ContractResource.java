package io.github.factoryfx.docu.systemtree;

import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/** lists the contract hash per published api class, checked by the control plane before deploying consumers */
@Path("contract")
public class ContractResource {

    private final Map<String, String> contractHashByApiClass;

    public ContractResource(Map<String, String> contractHashByApiClass) {
        this.contractHashByApiClass = Map.copyOf(contractHashByApiClass);
    }

    @GET
    @Path("{apiClass}")
    @Produces(MediaType.TEXT_PLAIN)
    public String contractHash(@PathParam("apiClass") String apiClass) {
        String hash = contractHashByApiClass.get(apiClass);
        if (hash == null) {
            throw new NotFoundException("no published api: " + apiClass);
        }
        return hash;
    }
}
