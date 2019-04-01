package io.github.factoryfx.factory;

import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * faster but less convenient Factory
 * provides utility methods
*/
public abstract class FastFactoryUtility {

    public static <R extends FactoryBase<?,R>,L, F extends FactoryBase<L,R>> void setup(Class<F> clazz, BiConsumer<F,AttributeVisitor> visitAttributesFlat, BiConsumer<F,Consumer<FactoryBase<?,R>>> visitDataChildren){
        FactoryMetadataManager.getMetadata(clazz).setVisitChildFactoriesAndViewsFlat(visitDataChildren);
        FactoryMetadataManager.getMetadata(clazz).setUseTemporaryAttributes();
        FactoryMetadataManager.getMetadata(clazz).setVisitAttributesFlat(visitAttributesFlat);
        FactoryMetadataManager.getMetadata(clazz).setVisitDataChildren((t, dataConsumer) -> visitDataChildren.accept(t, dataConsumer::accept));
    }

    public static <L,R extends FactoryBase<?,R>> L instance(FactoryBase<L,R> childFactory){
        L instance = null;
        if (childFactory!=null){
            instance = childFactory.internal().instance();
        }
        return instance;
    }

    public static <L,R extends FactoryBase<?,R>, F extends FactoryBase<L,R>> List<L> instances(List<F> childFactories){
        return childFactories.stream().map((f) -> f.internal().instance()).collect(Collectors.toList());
    }

    public static <V,A extends Attribute<V,A>> A tempAttributeSetup(A attribute, Consumer<V> setter, Supplier<V> getter){
        attribute.internal_addListener((attribute1, value) -> setter.accept(value));
        attribute.set(getter.get());
        return attribute;
    }

}
