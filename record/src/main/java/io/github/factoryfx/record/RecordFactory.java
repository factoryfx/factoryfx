package io.github.factoryfx.record;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.fastfactory.FastFactoryUtility;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.storage.migration.metadata.AttributeStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadata;

import java.util.ArrayList;
import java.util.function.Function;

//@JsonSerialize(using = RecordSerializer.class)
//@JsonDeserialize(using = RecordDeserializer.class)
public final class RecordFactory<L, D extends Dependencies<L>, R extends FactoryBase<?,R>> extends SimpleFactoryBase<L,R> {
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")
    @JsonProperty("dep")
    private final D dependencies;

    @JsonCreator
    public RecordFactory(@JsonProperty("dep") D dependencies) {
        this.dependencies = dependencies;
    }





    /**
     *
     * @return dependencies of this factory
     */
    public D dep() {
        return dependencies;
    }

    @Override
    protected L createImpl() {
        return dependencies.instance();
    }

    static {
        FastFactoryUtility.setup(RecordFactory.class, new RecordFactoryUtility());
        FactoryMetadataManager.getMetadata(RecordFactory.class).setNewCopyInstanceSupplier(new Function<RecordFactory, RecordFactory>() {
            @Override
            public RecordFactory<?,?,?> apply(RecordFactory recordFactory) {
                Dependencies<?> copyDependencies = recordFactory.dep().copy();
                return new RecordFactory<>(copyDependencies);
            }
        });
    }

    @Override
    public Class<?> internal_getDataClass() {
        return this.dep().getClass();
    }

    @Override
    public DataStorageMetadata internal_createDataStorageMetadata(long count) {
        ArrayList<AttributeStorageMetadata> attributes = new ArrayList<>();
        internal().visitAttributesFlat((attributeMetadata, attribute) -> {
            attributes.add(attribute.createAttributeStorageMetadata(attributeMetadata));
        });
        return new DataStorageMetadata(attributes,internal_getDataClass().getName(),count);
    }
}
