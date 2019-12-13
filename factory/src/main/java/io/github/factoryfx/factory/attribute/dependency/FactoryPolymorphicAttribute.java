package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.FactoryBase;
import java.util.function.Consumer;

/**
 * Attribute for polymorphic Reference.
 * Usually interface with different implementations
 *
 * @param <L> the base interface/class
 */
public class FactoryPolymorphicAttribute<L> extends FactoryBaseAttribute<L,FactoryBase<? extends L,?>, FactoryPolymorphicAttribute<L>> {

    public FactoryPolymorphicAttribute() {
        super();
    }

    /**
     * Explanation see: {@link FactoryAttribute#FactoryAttribute(Consumer)}}
     * @param setup setup function
     */
    public FactoryPolymorphicAttribute(Consumer<FactoryPolymorphicAttribute<L>> setup){
        super();
        setup.accept(this);
    }

}
