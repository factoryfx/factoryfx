package de.factoryfx.factory.atrribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceBaseAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.data.validation.ValidationResult;
import de.factoryfx.factory.FactoryBase;

import java.util.List;

/**
 * Attribute with factory
 * @param <F> Factory type in the attribute
 * @param <A> Attribute self

 */
public class FactoryReferenceBaseAttribute<L,F extends FactoryBase<? extends L,?,?>, A extends ReferenceBaseAttribute<F,F,A>> extends ReferenceAttribute<F,A> {



    @JsonCreator
    @SuppressWarnings("unchecked")
    protected FactoryReferenceBaseAttribute(F value) {
        super(value);
    }


    public FactoryReferenceBaseAttribute(Class<F> clazz) {
        this();
        setup(clazz);
    }

    @SuppressWarnings("unchecked")
    public FactoryReferenceBaseAttribute() {
        super();
    }


    public L instance(){
        if (get()==null){
            return null;
        }
        return get().internalFactory().instance();
    }

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
