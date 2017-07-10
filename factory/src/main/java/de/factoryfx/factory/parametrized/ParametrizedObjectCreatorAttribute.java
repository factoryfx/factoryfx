package de.factoryfx.factory.parametrized;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.Data;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

import java.util.Collection;
import java.util.function.Function;

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

    public L create(P p){
        return instance().create(p);
    }

    @Override
    public ParametrizedObjectCreatorAttribute<P, L, T> labelText(String text) {
        super.labelText(text);
        return this;
    }

    @Override
    public ParametrizedObjectCreatorAttribute<P, L, T> de(String text) {
        super.de(text);
        return this;
    }

    @Override
    public ParametrizedObjectCreatorAttribute<P, L, T> en(String text) {
        super.en(text);
        return this;
    }

    @Override
    public ParametrizedObjectCreatorAttribute<P, L, T> validation(Validation<T> validation) {
        super.validation(validation);
        return this;
    }

    @Override
    public ParametrizedObjectCreatorAttribute<P, L, T> possibleValueProvider(Function<Data, Collection<T>> provider) {
        super.possibleValueProvider(provider);
        return this;
    }
}
