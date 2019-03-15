package de.factoryfx.factory.parametrized;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.atrribute.FactoryReferenceBaseAttribute;

public class ParametrizedObjectCreatorAttribute<P, L, F extends ParametrizedObjectCreatorFactory<P,L,?,?>> extends FactoryReferenceBaseAttribute<ParametrizedObjectCreator<P,L>,F,ParametrizedObjectCreatorAttribute<P, L, F>> {

    @JsonCreator
    protected ParametrizedObjectCreatorAttribute(F value) {
        super(value);
    }


    public ParametrizedObjectCreatorAttribute(Class<F> clazz) {
        super();
        setup(clazz);
    }

    public ParametrizedObjectCreatorAttribute() {
        super();
    }

    public L create(P p){
        ParametrizedObjectCreator<P,L> instance = this.instance();
        return instance.create(p);
    }

    @Override
    public ParametrizedObjectCreatorAttribute<P,L,F> setupUnsafe(Class clazz){
        return super.setupUnsafe(clazz);
    }

    @Override
    public ParametrizedObjectCreatorAttribute<P,L,F> setup(Class<F> clazz){
        return super.setup(clazz);
    }

}
