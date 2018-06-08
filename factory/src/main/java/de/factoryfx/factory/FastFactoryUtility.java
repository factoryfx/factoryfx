package de.factoryfx.factory;

import de.factoryfx.data.AttributeVisitor;
import de.factoryfx.data.Data;
import de.factoryfx.data.DataDictionary;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeChangeListener;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

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

    public static <T extends FactoryBase<?,?,?>> void setup(Class<T> clazz,BiConsumer<T,AttributeVisitor> visitAttributesFlat, BiConsumer<T,Consumer<FactoryBase<?,?,?>>> visitDataChildren){
        FactoryDictionary.getFactoryDictionary(clazz).setVisitChildFactoriesAndViewsFlat(visitDataChildren);
        DataDictionary.getDataDictionary(clazz).setVisitAttributesFlat(visitAttributesFlat);
        DataDictionary.getDataDictionary(clazz).setVisitDataChildren((t, dataConsumer) -> visitDataChildren.accept(t, dataConsumer::accept));
    }

    public static <L,V,R extends FactoryBase<?,V,R>> L instance(FactoryBase<L,V,R> childFactory){
        L instance = null;
        if (childFactory!=null){
            instance = childFactory.internalFactory().instance();
        }
        return instance;
    }

    public static <L,V,R extends FactoryBase<?,V,R>, F extends FactoryBase<L,V,R>> List<L> instances(List<F> childFactories){
        return childFactories.stream().map((f) -> f.internalFactory().instance()).collect(Collectors.toList());
    }

    public static <V,A extends Attribute<V,A>> A tempAttributeSetup(A attribute, Consumer<V> setter, Supplier<V> getter){
        attribute.internal_addListener((attribute1, value) -> setter.accept(value));
        attribute.set(getter.get());
        return attribute;
    }

}
