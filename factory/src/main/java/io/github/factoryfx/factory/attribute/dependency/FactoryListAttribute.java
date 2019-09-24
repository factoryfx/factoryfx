package io.github.factoryfx.factory.attribute.dependency;


import io.github.factoryfx.factory.FactoryBase;

import java.util.function.Consumer;

/**
 * Attribute with factory list
 * @param <L> liveobject created form the factory
 * @param <F> factory
 */
public class FactoryListAttribute<L, F extends FactoryBase<L,?>> extends FactoryListBaseAttribute<L,F, FactoryListAttribute<L, F>> {

    public FactoryListAttribute(){
        super();
    }

    /**
     * Explanation see: {@link FactoryAttribute#FactoryAttribute(Consumer)}}
     * @param setup setup function
     */
    public FactoryListAttribute(Consumer<FactoryListAttribute<L,F>> setup){
        super();
        setup.accept(this);
    }

}
