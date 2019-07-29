package io.github.factoryfx.factory.attribute.dependency;


import io.github.factoryfx.factory.FactoryBase;

import java.util.function.Consumer;

/**
 * Attribute with factory
 * @param <L> liveobject created form the factory
 * @param <F> factory
 */
public class FactoryAttribute<L , F extends FactoryBase<L,?>> extends FactoryBaseAttribute<L,F, FactoryAttribute<L, F>> {

    public FactoryAttribute(){
        super();
    }

    /**
     * diamond operator doesn't work chained expression inference (Section D of JSR 335)<br>
     * <br>
     * e.g.:<br>
     *  <pre>{@code FactoryAttribute<LiveObject,Factory> attribute = new FactoryAttribute<LiveObject,Factory>().nullable();}</pre>
     *  Diamond Operator doesn't work:
     *  <pre>{@code FactoryAttribute<LiveObject,Factory> attribute = new FactoryAttribute<>().nullable();}</pre>
     *  Workaround:
     *  <pre>{@code FactoryAttribute<LiveObject,Factory> attribute = new FactoryAttribute<>(FactoryAttribute::nullable);}</pre>
     *
     * @param setup setup function
     */
    public FactoryAttribute(Consumer<FactoryAttribute<L,F>> setup){
        super();
        setup.accept(this);
    }
}


