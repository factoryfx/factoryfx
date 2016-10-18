package de.factoryfx.server.angularjs.factory.server.resourcehandler;

import java.util.UUID;

public class UuidETagProvider implements ETagProvider {
    private final String etag;
    public UuidETagProvider(){
        this.etag=UUID.randomUUID().toString();
    }


    @Override
    public String getEtag() {
        return etag;
    }
}
