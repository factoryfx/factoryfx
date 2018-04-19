package de.factoryfx.data.storage;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;

import java.util.function.Consumer;

public class JacksonDeSerialisation<R extends Data,S> implements DataDeSerialisation<R,S> {
    private final Class<R> rootClass;
    private final int dataModelVersion;
    private final Consumer<Attribute<?,?>> newAttributes;

    /**
     *
     * @param rootClass root class
     * @param dataModelVersion data model version
     * @param newAttributes  AttributeSetupHelper
     */
    public JacksonDeSerialisation(Class<R> rootClass, int dataModelVersion, Consumer<Attribute<?,?>> newAttributes) {
        this.rootClass = rootClass;
        this.dataModelVersion = dataModelVersion;
        this.newAttributes = newAttributes;
    }

    public JacksonDeSerialisation(Class<R> rootClass, int dataModelVersion) {
        this(rootClass, dataModelVersion, null);

    }

    @Override
    public boolean canRead(int dataModelVersion) {
        return this.dataModelVersion==dataModelVersion;
    }

    @Override
    public R read(String data) {
        return ObjectMapperBuilder.build().readValue(data,rootClass).internal().prepareUsableCopy(null,newAttributes);
    }

    @SuppressWarnings("unchecked")
    @Override
    public StoredDataMetadata<S> readStorageMetadata(String data) {
        return ObjectMapperBuilder.build().readValue(data,StoredDataMetadata.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ScheduledDataMetadata<S> readScheduledMetadata(String data) {
        return ObjectMapperBuilder.build().readValue(data,ScheduledDataMetadata.class);
    }
}
