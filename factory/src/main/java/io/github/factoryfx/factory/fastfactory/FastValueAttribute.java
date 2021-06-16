package io.github.factoryfx.factory.fastfactory;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.AttributeCopy;
import io.github.factoryfx.factory.attribute.AttributeMatch;
import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FastValueAttribute<R extends FactoryBase<?,R>,F extends FactoryBase<?,R>,V,A extends ImmutableValueAttribute<V,?>> extends FastFactoryAttributeUtility<R,F,V,A>{


    public FastValueAttribute(Supplier<A> attributeCreator, Function<F, V> valueGetter, BiConsumer<F,V> valueSetter, String attributeName) {
        super(attributeCreator,valueGetter,valueSetter,attributeName);
    }

    @Override
    public void visitChildFactory(Consumer<FactoryBase<?, ?>> consumer) {
        //nothing
    }

    public void internal_copyTo(AttributeCopy<V> copyAttribute, Function<FactoryBase<?, ?>, FactoryBase<?, ?>> newCopyInstanceProvider, final int level, final int maxLevel, final List<FactoryBase<?,?>> oldData, FactoryBase<?,?> parent, FactoryBase<?,?> root){
        copyAttribute.set(valueGetter.apply(boundFactory));
    }

    @Override
    public boolean internal_mergeMatch(AttributeMatch<V> value) {
        return Objects.equals(valueGetter.apply(boundFactory), value.get());
    }

//    @Override
//    public void internal_merge(V newValue){
//        valueSetter.accept(boundFactory,newValue);
//    }

    @Override
    @SuppressWarnings("unchecked")
    protected AttributeMetadata createAttributeMetadata() {
        return new AttributeMetadata(this.attributeName, (Class<? extends Attribute<?, ?>>) getAttribute().getClass(),null,null,null,getAttribute().internal_getLabelText(),getAttribute().internal_required());
    }

    @Override
    public void addBackReferencesToAttributes(F data, R root) {
        //nothing
    }
}
