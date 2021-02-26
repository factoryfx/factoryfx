package io.github.factoryfx.factory.fastfactory;

import io.github.factoryfx.factory.AttributeMetadataVisitor;
import io.github.factoryfx.factory.AttributeVisitor;
import io.github.factoryfx.factory.FactoryBase;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface FastFactoryUtilityInterface<R extends FactoryBase<?, R>, F extends FactoryBase<?, R>> {

    void visitAttributesFlat(F factory, AttributeVisitor attributeVisitor);

    void visitAttributesForCopy(F factory, F other, FactoryBase.BiCopyAttributeVisitor<?> consumer);

    <V> void visitAttributesForMatch(F factory, F other, FactoryBase.AttributeMatchVisitor<V> consumer);

    void visitChildFactoriesAndViewsFlat(F factory, Consumer<FactoryBase<?, ?>> consumer);

    void visitAttributesTripleFlat(F factory, F other1, F other2, FactoryBase.TriAttributeVisitor<?> consumer);

    void visitAttributesMetadataFlat(AttributeMetadataVisitor consumer);
}
