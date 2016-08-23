package de.factoryfx.adminui.angularjs.server.resourcehandler;

import java.util.UUID;

public class UuidETagProvider implements ETagProvider2 {
    private final String etag;
    public UuidETagProvider(){
        this.etag=UUID.randomUUID().toString();
    }


    @Override
    public String getEtag() {
        return etag;
    }
}
