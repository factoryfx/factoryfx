package io.github.factoryfx.factory.attribute;

import io.github.factoryfx.factory.metadata.AttributeMetadata;

public class AttributeAndMetadata {

    public final Attribute<?,?> attribute;
    public final AttributeMetadata attributeMetadata;

    public AttributeAndMetadata(Attribute<?, ?> attribute, AttributeMetadata attributeMetadata) {
        this.attribute = attribute;
        this.attributeMetadata = attributeMetadata;
    }
}
