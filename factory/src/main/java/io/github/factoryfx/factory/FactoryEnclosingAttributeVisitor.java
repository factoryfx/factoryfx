package io.github.factoryfx.factory;

import io.github.factoryfx.factory.attribute.dependency.FactoryChildrenEnclosingAttribute;

@FunctionalInterface
public interface FactoryEnclosingAttributeVisitor {
    void accept(String attributeVariableName, FactoryChildrenEnclosingAttribute attribute);
}
