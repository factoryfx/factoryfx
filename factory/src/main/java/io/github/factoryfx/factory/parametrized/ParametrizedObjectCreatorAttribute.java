package io.github.factoryfx.factory.parametrized;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;

public class ParametrizedObjectCreatorAttribute<R extends FactoryBase<?,R>,P, L, F extends ParametrizedObjectCreatorFactory<P,L,R>> extends FactoryBaseAttribute<R,ParametrizedObjectCreator<P,L>,F,ParametrizedObjectCreatorAttribute<R,P, L, F>> {

    public ParametrizedObjectCreatorAttribute() {
        super();
    }

    public L create(P p){
        ParametrizedObjectCreator<P,L> instance = this.instance();
        return instance.create(p);
    }

}
