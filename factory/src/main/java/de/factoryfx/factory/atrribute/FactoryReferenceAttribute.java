package de.factoryfx.factory.atrribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.data.validation.ValidationResult;
import de.factoryfx.factory.FactoryBase;

import java.util.List;

/**
 * Attribute with factory
 * @param <L> liveobject created form the factory
 * @param <F> factory
 */
public class FactoryReferenceAttribute<L, F extends FactoryBase<? extends L,?>> extends ReferenceAttribute<F,FactoryReferenceAttribute<L, F>> {

    private static final Validation requiredValidation = value -> {
        boolean error = value == null;
        return new ValidationResult(error, new LanguageText().en("required parameter").de("Pflichtparameter"));
    };

    @JsonCreator
    @SuppressWarnings("unchecked")
    protected FactoryReferenceAttribute(F value) {
        super(value);
    }


    public FactoryReferenceAttribute(Class<F> clazz) {
        this();
        setup(clazz);
    }

    @SuppressWarnings("unchecked")
    public FactoryReferenceAttribute() {
        super();
    }

    @Override
    public boolean internal_required() {
        return !nullable;
    }

    public L instance(){
        if (get()==null){
            return null;
        }
        return get().internalFactory().instance();
    }

    @Override
    public FactoryReferenceAttribute<L, F> setupUnsafe(Class clazz){
        return super.setupUnsafe(clazz);
    }

    @Override
    public FactoryReferenceAttribute<L, F> setup(Class<F> clazz){
        return super.setup(clazz);
    }

    private boolean nullable;
    public FactoryReferenceAttribute<L, F> nullable(){
        nullable=true;
        return this;
    }

    @Override
    public List<ValidationError> internal_validate(Data parent) {
        if (!nullable){
            this.validation(requiredValidation);// to minimise object creations
        }
        return super.internal_validate(parent);
    }
}
