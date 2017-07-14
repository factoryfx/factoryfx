package de.factoryfx.factory.parametrized;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ReferenceAttribute;

public class ParametrizedObjectCreatorAttribute<P, L, F extends ParametrizedObjectCreatorFactory<P,L,?>> extends ReferenceAttribute<F,ParametrizedObjectCreatorAttribute<P, L, F>> {

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
        return instance().create(p);
    }

    public ParametrizedObjectCreator<P,L> instance(){
        if (get()==null){
            return null;
        }
        return get().internalFactory().instance();
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
