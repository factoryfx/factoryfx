package de.factoryfx.data.storage;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;

public class JacksonSerialisation<R extends Data, S> implements DataSerialisation<R,S> {

    private final int dataModelVersion;

    public JacksonSerialisation(int dataModelVersion) {
        this.dataModelVersion = dataModelVersion;
    }

    @Override
    public String write(R root) {
        return ObjectMapperBuilder.build().writeValueAsString(root);
    }

    @Override
    public String writeStorageMetadata(StoredDataMetadata<S> metadata) {
        return ObjectMapperBuilder.build().writeValueAsString(new StoredDataMetadata<S>(
                metadata.creationTime,
                metadata.id,
                metadata.user,
                metadata.comment,
                metadata.baseVersionId,
                dataModelVersion,
                metadata.changeSummary
        ));
    }
}
