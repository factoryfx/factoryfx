package de.factoryfx.adminui.angularjs.server.resourcehandler;

import java.util.UUID;

public class UuidETagProvider2 implements ETagProvider {
    private final String etag;
    public UuidETagProvider2(){
        this.etag=UUID.randomUUID().toString();
    }


    @Override
    public String getEtag() {
        return etag;
    }
}
