package de.factoryfx.factory.parametrized;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class ParametrizedObjectCreatorAttribute<P, L, T extends ParametrizedObjectCreatorFactory<P,L,?>> extends FactoryReferenceAttribute<ParametrizedObjectCreator<P,L>,T> {

    @JsonCreator
    protected ParametrizedObjectCreatorAttribute(T value) {
        super(value);
    }


    public ParametrizedObjectCreatorAttribute(Class<T> clazz) {
        super();
        setup(clazz);
    }

    public ParametrizedObjectCreatorAttribute() {
        super();
    }


}
