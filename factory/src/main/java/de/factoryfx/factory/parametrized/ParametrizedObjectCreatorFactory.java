package de.factoryfx.factory.parametrized;

import de.factoryfx.factory.FactoryBase;

import java.util.function.Function;

public abstract class ParametrizedObjectCreatorFactory<P,L,V> extends FactoryBase<ParametrizedObjectCreator<P,L>,V> {


    public ParametrizedObjectCreatorFactory(){
        this.configLiveCycle().setCreator(() -> new ParametrizedObjectCreator<>(getCreator()));
    }

    protected abstract Function<P,L> getCreator();

}
