package io.github.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;

public class FactoryStyleValidatorBuilder {


    private final Function<FactoryValidatorUtility, List<FactoryStyleValidation>> fieldValidationAdder;
    private final Function<FactoryValidatorUtility, List<FactoryStyleValidation>> classValidationAdder;

    private static class FactoryValidatorUtility{
        private final Class<? extends FactoryBase<?,?>> factoryClass;
        private final Field field;

        public FactoryValidatorUtility(Class<? extends FactoryBase<?, ?>> factoryClass, Field field) {
            this.factoryClass = factoryClass;
            this.field = field;
        }

        public FactoryBase<?,?> create(){
            return FactoryMetadataManager.getMetadataUnsafe(factoryClass).newInstance();
        }

        public Field getField(){
            return field;
        }

        public Class<? extends FactoryBase<?,?>> getFactoryClass(){
            return factoryClass;
        }

    }

    public FactoryStyleValidatorBuilder(Function<FactoryValidatorUtility, List<FactoryStyleValidation>> validationAdder, Function<FactoryValidatorUtility, List<FactoryStyleValidation>> classValidationAdder) {
        this.fieldValidationAdder = validationAdder;
        this.classValidationAdder = classValidationAdder;
    }

    public FactoryStyleValidatorBuilder(){
        this((factoryValidatorUtility) -> {
            final ArrayList<FactoryStyleValidation> factoryStyleValidations = new ArrayList<>();
            factoryStyleValidations.add(new OnlyAttribute(factoryValidatorUtility.getFactoryClass(), factoryValidatorUtility.getField()));
            factoryStyleValidations.add(new NotNullAttributeValidation(factoryValidatorUtility.getFactoryClass()));
            factoryStyleValidations.add(new PublicValidation(factoryValidatorUtility.getFactoryClass(), factoryValidatorUtility.getField()));
            factoryStyleValidations.add(new FinalValidation(factoryValidatorUtility.getFactoryClass(), factoryValidatorUtility.getField()));
            factoryStyleValidations.add(new NoIdAsAttributeName(factoryValidatorUtility.getField()));
            return factoryStyleValidations;
        }, (factoryValidatorUtility)->List.of(new NotInnerClassValidation(factoryValidatorUtility.getFactoryClass())));
    }

    /** test if the model is valid:
     * all Attributes are public
     * all Attributes not null after instantiation
     * @param factoryClass factoryClass
     * @return validations
     * */
    public List<FactoryStyleValidation> createFactoryValidations(Class<? extends FactoryBase<?,?>> factoryClass){
        final ArrayList<FactoryStyleValidation> result = new ArrayList<>(classValidationAdder.apply(new FactoryValidatorUtility(factoryClass, null)));
        for (Field field: factoryClass.getDeclaredFields()){
            if (!field.getName().equals("$assertionsDisabled") && !field.getName().equals("$jacocoData")){//When the compiler finds an assertion in a class, it adds a generated static final field named $assertionsDisabled to the class.
                result.addAll(fieldValidationAdder.apply(new FactoryValidatorUtility(factoryClass,field)));
            }
        }
        return result;
    }

}
