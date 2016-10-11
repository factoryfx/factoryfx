package de.factoryfx.data.merge.testfactories;

import java.util.UUID;

import de.factoryfx.data.Data;

public class IdData extends Data {
    String id= UUID.randomUUID().toString();
    @Override
    public Object getId() {
        return id;
    }

    @Override
    public void setId(Object object) {
        id= (String) object;
    }
}
