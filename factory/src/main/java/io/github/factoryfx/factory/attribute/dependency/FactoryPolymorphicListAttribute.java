package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.FactoryBase;
import java.util.*;
import java.util.function.Consumer;

/**
 * Attribute for polymorphic Reference.
 * Usually interface with different implementations
 *
 * @param <L> the base interface/class
 */
public class FactoryPolymorphicListAttribute<L> extends FactoryListBaseAttribute<L,FactoryBase<? extends L,?>, FactoryPolymorphicListAttribute<L>> {


    public FactoryPolymorphicListAttribute() {
        super();
    }

    /**
     * Explanation see: {@link FactoryAttribute#FactoryAttribute(Consumer)}}
     * @param setup setup function
     */
    public FactoryPolymorphicListAttribute(Consumer<FactoryPolymorphicListAttribute<L>> setup){
        super();
        setup.accept(this);
    }

    @SuppressWarnings("unchecked")
    public <T extends FactoryBase> T get(Class<T> clazz) {
        for (FactoryBase<? extends L, ?> item : this.get()) {
            if (item.getClass()==clazz){
                return (T)item;
            }
        }
        return null;
    }

}
