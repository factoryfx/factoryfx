package io.github.factoryfx.factory.metadata;

import io.github.factoryfx.factory.FactoryBase;

public class AttributeMetadataAndAccessor<F extends FactoryBase<?,?>,V> {

    public final AttributeMetadata attributeMetadata;
    private final AttributeFieldAccessor<F,V> attributeFieldAccessor;

    public AttributeMetadataAndAccessor(AttributeMetadata attributeMetadata, AttributeFieldAccessor<F, V> attributeFieldAccessor) {
        this.attributeMetadata = attributeMetadata;
        this.attributeFieldAccessor = attributeFieldAccessor;
    }

    public V get(F factory) {
        return attributeFieldAccessor.get(factory);
    }

}
