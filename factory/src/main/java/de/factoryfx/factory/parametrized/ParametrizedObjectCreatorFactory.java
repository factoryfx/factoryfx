package de.factoryfx.factory.parametrized;

import de.factoryfx.factory.FactoryBase;

import java.util.function.Function;

public abstract class ParametrizedObjectCreatorFactory<P,L,R extends FactoryBase<?,R>> extends FactoryBase<ParametrizedObjectCreator<P,L>,R> {


    public ParametrizedObjectCreatorFactory(){
        this.configLifeCycle().setCreator(() -> new ParametrizedObjectCreator<>(getCreator()));
    }

    protected abstract Function<P,L> getCreator();

}
