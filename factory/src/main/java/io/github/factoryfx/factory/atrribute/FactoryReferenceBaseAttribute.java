package io.github.factoryfx.factory.atrribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.ReferenceAttribute;
import io.github.factoryfx.data.attribute.ReferenceBaseAttribute;
import io.github.factoryfx.data.util.LanguageText;
import io.github.factoryfx.data.validation.Validation;
import io.github.factoryfx.data.validation.ValidationError;
import io.github.factoryfx.data.validation.ValidationResult;
import io.github.factoryfx.factory.FactoryBase;

import java.util.List;

/**
 * Attribute with factory
 * @param <F> Factory type in the attribute
 * @param <A> Attribute self

 */
public class FactoryReferenceBaseAttribute<L,F extends FactoryBase<? extends L,?>, A extends ReferenceBaseAttribute<F,F,A>> extends ReferenceAttribute<F,A> {



    @JsonCreator
    protected FactoryReferenceBaseAttribute(F value) {
        super(value);
    }


    public FactoryReferenceBaseAttribute(Class<F> clazz) {
        this();
        setup(clazz);
    }

    public FactoryReferenceBaseAttribute() {
        super();
    }


    public L instance(){
        if (get()==null){
            return null;
        }
        return get().internalFactory().instance();
    }

//    @SuppressWarnings("unchecked")
//    public <LO> LO instance(){
//        if (get()==null){
//            return null;
//        }
//        return (LO)get().internalFactory().instance();
//    }

    @Override
    public boolean internal_required() {
        return !nullable;
    }

    private static final Validation requiredValidation = value -> {
        boolean error = value == null;
        return new ValidationResult(error, new LanguageText().en("required parameter").de("Pflichtparameter"));
    };

    private boolean nullable;

    @SuppressWarnings("unchecked")
    public A nullable(){
        nullable=true;
        return (A)this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ValidationError> internal_validate(Data parent,String attributeVariableName) {
        if (!nullable){
            this.validation(requiredValidation);// to minimise object creations
        }
        return super.internal_validate(parent,attributeVariableName);
    }
}
