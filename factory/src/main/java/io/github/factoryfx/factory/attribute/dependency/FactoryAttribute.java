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

    /**
     * diamond operator doesn't work chained expression inference (Section D of JSR 335)<br>
     * <br>
     * e.g.:<br>
     *  <code>FactoryAttribute<LiveObject,Factory> attribute = new FactoryAttribute<LiveObject,Factory>().nullable();</code><br>
     *  Diamond Operator doesn't work:
     *  <code>FactoryAttribute<LiveObject,Factory> attribute = new FactoryAttribute<>().nullable();</code><br>
     *  Workaround:
     *  <code>FactoryAttribute<LiveObject,Factory> attribute = new FactoryAttribute<>(FactoryAttribute::nullable);</code><br>
     *
     * @param setup setup function
     */
    public FactoryAttribute(Consumer<FactoryAttribute<R,L,F>> setup){
        super();
        setup.accept(this);
    }
}


