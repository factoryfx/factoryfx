package io.github.factoryfx.factory.attribute.dependency;


import io.github.factoryfx.factory.FactoryBase;

import java.util.function.Consumer;

/**
 * Attribute with factory
 * @param <L> liveobject created form the factory
 * @param <F> factory
 */
public class FactoryAttribute<R extends FactoryBase<?,R>,L , F extends FactoryBase<L,R>> extends FactoryBaseAttribute<R,L,F, FactoryAttribute<R,L, F>> {

    public FactoryAttribute(){
        super();
    }

    public FactoryAttribute(Consumer<FactoryAttribute<R,L,F>> setup){
        super();
        setup.accept(this);
    }
}


