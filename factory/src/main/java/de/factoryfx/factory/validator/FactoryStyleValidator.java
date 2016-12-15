package de.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.factory.FactoryBase;

public class FactoryStyleValidator {


    private final BiFunction<FactoryBase<?, ?>, Field,List<FactoryStyleValidation>> fieldValidationAdder;
    private final Function<FactoryBase<?, ?>,List<FactoryStyleValidation>> factoryValidationAdder;

    public FactoryStyleValidator(BiFunction<FactoryBase<?, ?>, Field, List<FactoryStyleValidation>> validationAdder, Function<FactoryBase<?, ?>,List<FactoryStyleValidation>> factoryValidationAdder) {
        this.fieldValidationAdder = validationAdder;
        this.factoryValidationAdder = factoryValidationAdder;
    }

    public FactoryStyleValidator(){
        this((factoryBase, field) -> {
            final ArrayList<FactoryStyleValidation> factoryStyleValidations = new ArrayList<>();
            factoryStyleValidations.add(new NoReferenceAttribute(factoryBase, field));
            factoryStyleValidations.add(new NotNullAttributeValidation(factoryBase, field));
            factoryStyleValidations.add(new PublicValidation(factoryBase, field));
            factoryStyleValidations.add(new FinalValidation(factoryBase, field));
            return factoryStyleValidations;
        }, factoryBase -> {
            final ArrayList<FactoryStyleValidation> factoryStyleValidations = new ArrayList<>();
            return factoryStyleValidations;
        });
    }

    /** test if the model is valid:
     * all Attributes are public
     * all Attributes not null after instantiation*/
    public List<FactoryStyleValidation> createFactoryValidations(FactoryBase<?,?> factoryBase){
        final ArrayList<FactoryStyleValidation> result = new ArrayList<>();
        for (Field field: factoryBase.getClass().getDeclaredFields()){

            if (Attribute.class.isAssignableFrom(field.getType())){
                result.addAll(fieldValidationAdder.apply(factoryBase,field));
            }
        }
        result.addAll(factoryValidationAdder.apply(factoryBase));
        return result;
    }

}
