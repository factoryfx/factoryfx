package io.github.factoryfx.factory.fastfactory;

import io.github.factoryfx.factory.AttributeMetadataVisitor;
import io.github.factoryfx.factory.AttributeVisitor;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.AttributeCopy;
import io.github.factoryfx.factory.attribute.AttributeMatch;
import io.github.factoryfx.factory.attribute.AttributeMerger;
import io.github.factoryfx.factory.metadata.AttributeMetadata;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class FastFactoryAttributeUtility<R extends FactoryBase<?,R>, F extends FactoryBase<?,R>, V, A extends Attribute<V,?>> implements AttributeCopy<V>, AttributeMatch<V>, AttributeMerger<V> {
    private A attribute;
    private final Supplier<A> attributeCreator;
    protected String attributeName;
    protected final Function<F,V> valueGetter;
    protected final BiConsumer<F,V> valueSetter;

    public FastFactoryAttributeUtility(Supplier<A> attributeCreator, Function<F,V> valueGetter, BiConsumer<F,V> valueSetter, String attributeName) {
        this.attributeCreator = attributeCreator;
        this.valueGetter = valueGetter;
        this.valueSetter = valueSetter;
        this.attributeName = attributeName;
    }

    protected A getAttribute(){
        if (attribute==null){
            attribute=attributeCreator.get();
        }
        return attribute;
    }

    public void setAttribute(F factory) {
        getAttribute().internal_reset();
        getAttribute().set(valueGetter.apply(factory));
        getAttribute().internal_addListener((attribute1, value) -> valueSetter.accept(factory,value));
    }

    public void setAttributeName(String attributeName) {
        this.attributeName=attributeName;
    }

    public void accept(AttributeVisitor attributeVisitor) {
        attributeVisitor.accept(createAttributeMetadata(),attribute);
    }

    public void accept(FastFactoryAttributeUtility<R,F,V,?> otherAttribute, FactoryBase.BiCopyAttributeVisitor<V> consumer) {
        consumer.accept(this,otherAttribute);
    }

    public void accept(FastFactoryAttributeUtility<R,F,V,?> otherAttribute, FactoryBase.AttributeMatchVisitor<V> consumer) {
        consumer.accept(attributeName,this,otherAttribute);
    }

    public void accept(FastFactoryAttributeUtility<R,F,V,?> otherAttribute1, FastFactoryAttributeUtility<R,F,V,?> otherAttribute2, FactoryBase.TriAttributeVisitor<V> consumer) {
        consumer.accept(attributeName,this,otherAttribute1,otherAttribute2);
    }

    public abstract void visitChildFactory(Consumer<FactoryBase<?,?>> consumer);

    F boundFactory;
    public void bindFactory(F factory){
        this.boundFactory =factory;
    }
    @Override
    public void internal_addBackReferences(FactoryBase<?,?> root, FactoryBase<?,?> parent){
        //nothing
    }
    @Override
    public void set(V value){
        this.valueSetter.accept(boundFactory,value);
    }

    @Override
    public V get() {
        return valueGetter.apply(boundFactory);
    }

    @Override
    public boolean internal_hasWritePermission(Function<String,Boolean> permissionChecker){
        return true;
    }


    public void visitAttributesMetadataFlat(AttributeMetadataVisitor consumer){
        consumer.accept(createAttributeMetadata());
    }

    protected abstract AttributeMetadata createAttributeMetadata();

    public void addBackReferencesToAttributes(F data, R root){
        getAttribute().internal_addBackReferences(data,root);
    }
}
