package io.github.factoryfx.factory.fastfactory;

import io.github.factoryfx.factory.AttributeVisitor;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * faster but less convenient Factory
 * provides utility methods
*/
public class FastFactoryUtility<R extends FactoryBase<?,R>,F extends FactoryBase<?,R>> {

    public static <L,R extends FactoryBase<?,R>,F extends FactoryBase<L,R>> void setup(Class<F> clazz, FastFactoryUtility<R,F> fastFactoryUtility ){
        FactoryMetadataManager.getMetadata(clazz).setUseTemporaryAttributes();
        FactoryMetadataManager.getMetadata(clazz).setFastFactoryUtility(fastFactoryUtility);
    }


    List<? extends FastFactoryAttributeUtility<R,F,?,?>> attributeList1;
    List<? extends FastFactoryAttributeUtility<R,F,?,?>> attributeList2;

    Supplier<List<? extends FastFactoryAttributeUtility<R,F,?,?>>> attributesCreator;

    public FastFactoryUtility(Supplier<List<? extends FastFactoryAttributeUtility<R,F,?,?>>> attributesCreator){
        attributeList1=attributesCreator.get();
        attributeList2=attributesCreator.get();

        this.attributesCreator= attributesCreator;

        for (int i = 0; i < attributeList1.size(); i++) {
            attributeList1.get(i).setAttributeName("dynamicAttribute"+i);
            attributeList2.get(i).setAttributeName("dynamicAttribute"+i);
        }
    }

    public void visitAttributesFlat(F factory, AttributeVisitor attributeVisitor) {
        for (FastFactoryAttributeUtility<R, F,?, ?> attributeUtility : attributeList1) {
            attributeUtility.setAttribute(factory);
        }
        for (FastFactoryAttributeUtility<R, F,?, ?> attributeUtility : attributeList1) {
            attributeUtility.accept(attributeVisitor);
        }
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

    public void visitAttributesForCopy(F factory, F other, FactoryBase.BiCopyAttributeVisitor<?> consumer) {
        for (FastFactoryAttributeUtility<R, F, ?, ?> attributeUtility : attributeList1) {
            attributeUtility.bindFactory(factory);
        }
        for (FastFactoryAttributeUtility<R, F, ?, ?> attributeUtility : attributeList2) {
            attributeUtility.bindFactory(other);
        }
        for (int i = 0; i < attributeList1.size(); i++) {
            visitAttributeForCopy(attributeList1.get(i),attributeList2.get(i),consumer);
        }
    }

    @SuppressWarnings("unchecked")
    private  <V> void visitAttributeForCopy(FastFactoryAttributeUtility<R, F, ?, ?> factory, FastFactoryAttributeUtility<R, F, ?, ?> other, FactoryBase.BiCopyAttributeVisitor<V> consumer) {
        consumer.accept((FastFactoryAttributeUtility<R, F, V, ?>)factory,(FastFactoryAttributeUtility<R, F, V, ?>)other);
    }


    public <V> void visitAttributesForMatch(F factory, F other, FactoryBase.AttributeMatchVisitor<V> consumer) {
        for (FastFactoryAttributeUtility<R, F, ?, ?> attributeUtility : attributeList1) {
            attributeUtility.bindFactory(factory);
        }
        for (FastFactoryAttributeUtility<R, F, ?, ?> attributeUtility : attributeList2) {
            attributeUtility.bindFactory(other);
        }
        for (int i = 0; i < attributeList1.size(); i++) {
            visitAttributeForMatch(attributeList1.get(i),attributeList2.get(i),consumer);
        }
    }

    @SuppressWarnings("unchecked")
    private  <V> void visitAttributeForMatch(FastFactoryAttributeUtility<R, F, ?, ?> factory, FastFactoryAttributeUtility<R, F, ?, ?> other, FactoryBase.AttributeMatchVisitor<V> consumer) {
       ((FastFactoryAttributeUtility<R, F, V, ?>)factory).accept((FastFactoryAttributeUtility<R, F, V, ?>)other,consumer);
    }

    public void visitChildFactoriesAndViewsFlat(F factory, Consumer<FactoryBase<?, R>> consumer) {
        for (FastFactoryAttributeUtility<R, F, ?, ?> attributeUtility : attributeList1) {
            attributeUtility.bindFactory(factory);
            attributeUtility.visitChildFactory(consumer);
        }
    }

    public void visitAttributesTripleFlat(F factory, F other1, F other2, FactoryBase.TriAttributeVisitor<?> consumer) {
        List<? extends FastFactoryAttributeUtility<R,F,?,?>> attributeList1=attributesCreator.get();//recreate for the delayed merge (merge is executed after the visit)
        List<? extends FastFactoryAttributeUtility<R,F,?,?>> attributeList2=attributesCreator.get();
        List<? extends FastFactoryAttributeUtility<R,F,?,?>> attributeList3=attributesCreator.get();


        for (FastFactoryAttributeUtility<R, F, ?, ?> attributeUtility : attributeList1) {
            attributeUtility.bindFactory(factory);
        }
        for (FastFactoryAttributeUtility<R, F, ?, ?> attributeUtility : attributeList2) {
            attributeUtility.bindFactory(other1);
        }
        for (FastFactoryAttributeUtility<R, F, ?, ?> attributeUtility : attributeList3) {
            attributeUtility.bindFactory(other2);
        }

        for (int i = 0; i < attributeList1.size(); i++) {
            visitAttributeTripleFlat(attributeList1.get(i),attributeList2.get(i),attributeList3.get(i),consumer);
        }
    }

    @SuppressWarnings("unchecked")
    private <V> void visitAttributeTripleFlat(FastFactoryAttributeUtility<R, F, ?, ?> factory, FastFactoryAttributeUtility<R, F, ?, ?> other1,FastFactoryAttributeUtility<R, F, ?, ?> other2, FactoryBase.TriAttributeVisitor<V> consumer) {
        ((FastFactoryAttributeUtility<R, F, V, ?>)factory).accept((FastFactoryAttributeUtility<R, F, V, ?>)other1,(FastFactoryAttributeUtility<R, F, V, ?>)other2,consumer);
    }


}
