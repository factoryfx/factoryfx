package io.github.factoryfx.factory;

import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;

@FunctionalInterface
public interface AttributeVisitor {
    void accept(AttributeMetadata attributeMetadata, Attribute<?, ?> attribute);
}
