package de.factoryfx.data;

import de.factoryfx.data.attribute.Attribute;

@FunctionalInterface
public interface AttributeVisitor {
    void accept(String attributeVariableName, Attribute<?, ?> attribute);
}
