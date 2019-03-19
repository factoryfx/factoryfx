package io.github.factoryfx.factory.parametrized;

import java.util.function.Function;

public class ParametrizedObjectCreator<P,L>{

    private final Function<P,L> creator;

    public ParametrizedObjectCreator(Function<P, L> creator) {
        this.creator = creator;
    }

    public L create(P transientParameter){
        return creator.apply(transientParameter);
    }
}
