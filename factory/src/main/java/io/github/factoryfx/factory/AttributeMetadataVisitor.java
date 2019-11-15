package io.github.factoryfx.factory;

import io.github.factoryfx.factory.attribute.Attribute;
import org.w3c.dom.Attr;

@FunctionalInterface
public interface AttributeMetadataVisitor {
    void accept(String attributeVariableName, Class<? extends Attribute<?,?>> attributeClass, Class<? extends FactoryBase<?,?>> referenceClass);
}
