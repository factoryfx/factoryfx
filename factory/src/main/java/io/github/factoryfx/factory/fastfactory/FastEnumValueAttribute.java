package io.github.factoryfx.factory.fastfactory;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.types.EnumAttribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FastEnumValueAttribute<R extends FactoryBase<?,R>,F extends FactoryBase<?,R>,V extends Enum<V>> extends FastValueAttribute<R,F,V,EnumAttribute<V>>{

    private final Class<? extends Enum<?>> enumClass;
    public FastEnumValueAttribute(Supplier<EnumAttribute<V>> attributeCreator, Function<F, V> valueGetter, BiConsumer<F,V> valueSetter, String attributeName, Class<? extends Enum<?>> enumClass) {
        super(attributeCreator,valueGetter,valueSetter,attributeName);
        this.enumClass= enumClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AttributeMetadata createAttributeMetadata() {
        return new AttributeMetadata(this.attributeName, (Class<? extends Attribute<?, ?>>) getAttribute().getClass(),null,null,this.enumClass,getAttribute().internal_getLabelText(),getAttribute().internal_required());
    }
}
