package io.github.factoryfx.factory;

import io.github.factoryfx.factory.attribute.dependency.FactoryChildrenEnclosingAttribute;

@FunctionalInterface
public interface FactoryEnclosingAttributeVisitor<R extends FactoryBase<?,R>> {
    void accept(String attributeVariableName, FactoryChildrenEnclosingAttribute<R, ?> attribute);
}
