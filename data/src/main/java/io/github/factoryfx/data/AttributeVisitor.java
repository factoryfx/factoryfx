package io.github.factoryfx.data;

import io.github.factoryfx.data.attribute.Attribute;

@FunctionalInterface
public interface AttributeVisitor {
    void accept(String attributeVariableName, Attribute<?, ?> attribute);
}
