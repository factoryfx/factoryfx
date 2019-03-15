package de.factoryfx.factory.atrribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.data.validation.ValidationResult;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.PolymorphicFactory;

import java.util.*;

/**
 * Attribute for polymorphic Reference.
 * Usually interface with different implementations
 *
 * @param <L> the base interface/class
 */
public class FactoryPolymorphicReferenceAttribute<L> extends FactoryReferenceBaseAttribute<L,FactoryBase<? extends L,?>,FactoryPolymorphicReferenceAttribute<L>> {

    private static final Validation requiredValidation = value -> {
        boolean error = value == null;
        return new ValidationResult(error, new LanguageText().en("required parameter").de("Pflichtparameter"));
    };

    @JsonCreator
    protected FactoryPolymorphicReferenceAttribute(FactoryBase<L,?> value) {
        super(value);
    }

    public FactoryPolymorphicReferenceAttribute() {
        super();
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public FactoryPolymorphicReferenceAttribute(Class<L> liveObjectClass, Class<? extends PolymorphicFactory<?>>... possibleFactoriesClasses) {
        super();
        setup(liveObjectClass,possibleFactoriesClasses);
        this.validation(requiredValidation);
    }


    private List<Class<?>> possibleFactoriesClasses;


    /**
     * workaround: if possibleFactoriesClasses has generic parameter the normal setup method doesn't work
     * @param liveObjectClass liveObjectClass
     * @param possibleFactoriesClasses possibleFactoriesClasses
     * @return self
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final FactoryPolymorphicReferenceAttribute<L> setupUnsafe(Class liveObjectClass, Class... possibleFactoriesClasses){
        this.possibleFactoriesClasses=Arrays.asList(possibleFactoriesClasses);
        for (Class clazz: possibleFactoriesClasses){
            if (!FactoryBase.class.isAssignableFrom(clazz)){
                throw new IllegalArgumentException("parameter must be a factory: "+clazz);
            }
        }
        return setup(liveObjectClass,possibleFactoriesClasses);
    }

    /**
     * setup for select and new value editing
     * @param liveObjectClass type of liveobject
     * @param possibleFactoriesClasses possible factories that crate the liveobject, PolymorphicFactory&lt;L&gt; would be correct but doesn't work
     * @return self
     */
    @SafeVarargs
    public final FactoryPolymorphicReferenceAttribute<L> setup(Class<L> liveObjectClass, Class<? extends PolymorphicFactory<?>>... possibleFactoriesClasses){
        this.possibleFactoriesClasses=Arrays.asList(possibleFactoriesClasses);
        new FactoryPolymorphicUtil<L>().setup(this,liveObjectClass,()->this.root,possibleFactoriesClasses);
        return this;
    }


    /**
     * intended to be used from code generators
     * @return list of possible classes
     * */
    public List<Class<?>> internal_possibleFactoriesClasses(){
        return possibleFactoriesClasses;
    }


}
