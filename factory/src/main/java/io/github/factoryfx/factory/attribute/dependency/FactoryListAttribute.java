package io.github.factoryfx.factory.attribute.dependency;


import io.github.factoryfx.factory.FactoryBase;

import java.util.function.Consumer;

/**
 * Attribute with factory
 * @param <L> liveobject created form the factory
 * @param <F> factory
 */
public class FactoryListAttribute<R extends FactoryBase<?,R>,L, F extends FactoryBase<L,R>> extends FactoryListBaseAttribute<R,L,F, FactoryListAttribute<R,L, F>> {

    public FactoryListAttribute(){
        super();
    }

    /**
     * Explanation see: {@link FactoryAttribute#FactoryAttribute(Consumer)}}
     * @param setup setup function
     */
    public FactoryListAttribute(Consumer<FactoryListAttribute<R,L,F>> setup){
        super();
        setup.accept(this);
    }

}
