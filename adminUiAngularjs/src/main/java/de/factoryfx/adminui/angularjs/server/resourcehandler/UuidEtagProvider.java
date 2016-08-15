package de.factoryfx.adminui.angularjs.server.resourcehandler;

import java.util.UUID;

public class UuidEtagProvider implements EtagProvider {
    private final String etag;
    public UuidEtagProvider(){
        this.etag=UUID.randomUUID().toString();
    }


    @Override
    public String getEtag() {
        return etag;
    }
}
