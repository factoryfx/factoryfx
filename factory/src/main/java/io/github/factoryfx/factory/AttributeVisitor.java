package io.github.factoryfx.factory;

import io.github.factoryfx.factory.attribute.Attribute;

@FunctionalInterface
public interface AttributeVisitor {
    void accept(String attributeVariableName, Attribute<?, ?> attribute);
}
