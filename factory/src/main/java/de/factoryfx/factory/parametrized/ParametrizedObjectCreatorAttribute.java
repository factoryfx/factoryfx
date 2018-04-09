package de.factoryfx.factory.parametrized;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.data.validation.ValidationResult;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttributeBase;

import java.util.List;

public class ParametrizedObjectCreatorAttribute<P, L, F extends ParametrizedObjectCreatorFactory<P,L,?>> extends FactoryReferenceAttributeBase<ParametrizedObjectCreator<P,L>,F,ParametrizedObjectCreatorAttribute<P, L, F>> {

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

    @Override
    public ParametrizedObjectCreatorAttribute<P,L,F> setupUnsafe(Class clazz){
        return super.setupUnsafe(clazz);
    }

    @Override
    public ParametrizedObjectCreatorAttribute<P,L,F> setup(Class<F> clazz){
        return super.setup(clazz);
    }

}
